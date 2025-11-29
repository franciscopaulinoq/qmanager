package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.TicketRequest;
import io.github.franciscopaulinoq.qmanager.dto.TicketResponse;
import io.github.franciscopaulinoq.qmanager.exception.CategoryNotFoundException;
import io.github.franciscopaulinoq.qmanager.exception.PriorityNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.TicketMapper;
import io.github.franciscopaulinoq.qmanager.model.Ticket;
import io.github.franciscopaulinoq.qmanager.model.TicketStatus;
import io.github.franciscopaulinoq.qmanager.repository.CategoryRepository;
import io.github.franciscopaulinoq.qmanager.repository.PriorityRepository;
import io.github.franciscopaulinoq.qmanager.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketSequenceService ticketSequenceService;
    private final TicketRepository repository;
    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final TicketMapper mapper;

    @Transactional
    public TicketResponse create(TicketRequest request) {
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

        ticket = repository.save(ticket);

        return mapper.toResponse(ticket);
    }
}
