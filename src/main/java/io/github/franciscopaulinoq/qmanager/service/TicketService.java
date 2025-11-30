package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketResponse;
import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketUpdateRequest;
import io.github.franciscopaulinoq.qmanager.exception.BusinessException;
import io.github.franciscopaulinoq.qmanager.exception.CategoryNotFoundException;
import io.github.franciscopaulinoq.qmanager.exception.PriorityNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.TicketMapper;
import io.github.franciscopaulinoq.qmanager.model.Ticket;
import io.github.franciscopaulinoq.qmanager.model.TicketStatus;
import io.github.franciscopaulinoq.qmanager.repository.CategoryRepository;
import io.github.franciscopaulinoq.qmanager.repository.PriorityRepository;
import io.github.franciscopaulinoq.qmanager.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    @Value("${qmanager.queue.strategy:STRICT}")
    private String strategy;

    @Value("${qmanager.queue.max.call.attempts}")
    private int maxCallAttempts;

    private final TicketSequenceService ticketSequenceService;
    private final TicketRepository repository;
    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final TicketMapper mapper;

    @Transactional
    public TicketResponse create(TicketCreateRequest request) {
        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        var priority = priorityRepository.findById(request.priorityId())
                .orElseThrow(() -> new PriorityNotFoundException("Priority not found"));

        String prefix = category.getPrefix() + priority.getPrefix();
        int number = ticketSequenceService.getNextSequence(prefix);
        String code = String.format("%s-%03d", prefix, number);

        var ticket = Ticket.builder()
                .code(code)
                .category(category)
                .priority(priority)
                .status(TicketStatus.WAITING)
                .callCount(0)
                .issueDate(LocalDate.now())
                .build();

        var saved = repository.save(ticket);

        return mapper.toResponse(saved);
    }

    @Transactional
    public TicketResponse callNextTicket() {
        Optional<Ticket> nextTicketOpt;

        if ("FIFO".equals(strategy)) {
            nextTicketOpt = repository.findNextFifo();
        } else {
            nextTicketOpt = repository.findNextStrict();
        }

        var ticket = nextTicketOpt
                .orElseThrow(() -> new BusinessException("No one on queue."));

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setCalledAt(OffsetDateTime.now());
        ticket.setCallCount(1);

        return mapper.toResponse(repository.save(ticket));
    }

    @Transactional
    public TicketResponse updateStatus(UUID id, TicketUpdateRequest request) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CLOSED ||
                ticket.getStatus() == TicketStatus.EXPIRED) {
            throw new BusinessException("This ticket has been closed");
        }

        ticket.setStatus(request.status());

        if (request.status() == TicketStatus.CLOSED) {
            ticket.setClosedAt(OffsetDateTime.now());
        }

        return mapper.toResponse(repository.save(ticket));
    }

    @Transactional
    public TicketResponse moveToPending(UUID id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new BusinessException("Only in-progress tickets can be placed on hold");
        }

        int newCount = ticket.getCallCount() + 1;
        ticket.setCallCount(newCount);

        if (newCount > maxCallAttempts) {
            ticket.setStatus(TicketStatus.EXPIRED);
        } else {
            ticket.setStatus(TicketStatus.PENDING);
        }

        return mapper.toResponse(repository.save(ticket));
    }

    @Transactional
    public TicketResponse reactivateTicket(UUID id, boolean urgent) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new BusinessException("Only pending tickets can be reactivated");
        }

        if (urgent) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticket.setCalledAt(OffsetDateTime.now());
        } else {
            ticket.setStatus(TicketStatus.WAITING);
            ticket.setCreatedAt(OffsetDateTime.now());
        }

        return mapper.toResponse(repository.save(ticket));
    }

    public Page<TicketResponse> listAllTickets(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
