package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.TicketCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.TicketMonitorResponse;
import io.github.franciscopaulinoq.qmanager.dto.TicketResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {
    @Value("${qmanager.queue.strategy:STRICT}")
    private String strategy;

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

    public TicketMonitorResponse getTicketMonitor() {
        List<Ticket> recentHistory = repository.findRecentHistory(LocalDate.now());

        TicketResponse current = null;
        List<TicketResponse> historyResponse;

        if (!recentHistory.isEmpty()) {
            Ticket latest = recentHistory.getFirst();

            if (latest.getStatus() == TicketStatus.IN_PROGRESS) {
                current = mapper.toResponse(latest);
                historyResponse = mapper.toResponse(recentHistory.subList(1, recentHistory.size()));
            } else {
                historyResponse = mapper.toResponse(recentHistory);
            }
        } else {
            historyResponse = List.of();
        }

        return new TicketMonitorResponse(current, historyResponse);
    }

    public Page<TicketResponse> listAllTickets(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
