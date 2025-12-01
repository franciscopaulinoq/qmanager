package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketResponse;
import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketUpdateRequest;
import io.github.franciscopaulinoq.qmanager.exception.BusinessException;
import io.github.franciscopaulinoq.qmanager.exception.CategoryNotFoundException;
import io.github.franciscopaulinoq.qmanager.exception.PriorityNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.TicketMapper;
import io.github.franciscopaulinoq.qmanager.model.Category;
import io.github.franciscopaulinoq.qmanager.model.Priority;
import io.github.franciscopaulinoq.qmanager.model.Ticket;
import io.github.franciscopaulinoq.qmanager.model.TicketStatus;
import io.github.franciscopaulinoq.qmanager.repository.CategoryRepository;
import io.github.franciscopaulinoq.qmanager.repository.PriorityRepository;
import io.github.franciscopaulinoq.qmanager.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @Mock
    private TicketSequenceService ticketSequenceService;

    @Mock
    private TicketRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PriorityRepository priorityRepository;

    @Mock
    private TicketMapper mapper;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ticketService, "strategy", "STRICT");
        ReflectionTestUtils.setField(ticketService, "maxCallAttempts", 3);
    }

    @Test
    @DisplayName("Create should return TicketResponse when data is valid")
    void createShouldReturnTicketResponseWhenDataIsValid() {
        UUID categoryId = UUID.randomUUID();
        UUID priorityId = UUID.randomUUID();
        var request = TicketCreateRequest.builder()
                .categoryId(categoryId)
                .priorityId(priorityId)
                .build();

        var category = Category.builder()
                .id(categoryId)
                .prefix("N")
                .build();

        var priority = Priority.builder()
                .id(priorityId)
                .prefix("G")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(priorityRepository.findById(priorityId)).thenReturn(Optional.of(priority));
        when(ticketSequenceService.getNextSequence("NG")).thenReturn(1);

        var savedTicket = Ticket.builder()
                .code("NG-001")
                .build();

        when(repository.save(any(Ticket.class))).thenReturn(savedTicket);

        var expectedResponse = TicketResponse.builder()
                .id(savedTicket.getId())
                .code("NG-001")
                .build();
        when(mapper.toResponse(savedTicket)).thenReturn(expectedResponse);

        var response = ticketService.create(request);

        assertNotNull(response);
        assertEquals("NG-001", response.code());

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(repository).save(ticketCaptor.capture());

        Ticket capturedTicket = ticketCaptor.getValue();
        assertEquals("NG-001", capturedTicket.getCode());
        assertEquals(TicketStatus.WAITING, capturedTicket.getStatus());
        assertEquals(0, capturedTicket.getCallCount());
        assertEquals(LocalDate.now(), capturedTicket.getIssueDate());
    }

    @Test
    @DisplayName("Create should throw CategoryNotFoundException when category does not exist")
    void createShouldThrowExceptionWhenCategoryNotFound() {
        var request = TicketCreateRequest.builder()
                .categoryId(UUID.randomUUID())
                .priorityId(UUID.randomUUID())
                .build();

        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> ticketService.create(request));

        verify(priorityRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Create should throw PriorityNotFoundException when priority does not exist")
    void createShouldThrowExceptionWhenPriorityNotFound() {
        var request = TicketCreateRequest.builder()
                .categoryId(UUID.randomUUID())
                .priorityId(UUID.randomUUID())
                .build();

        when(categoryRepository.findById(any())).thenReturn(Optional.of(new Category()));
        when(priorityRepository.findById(request.priorityId())).thenReturn(Optional.empty());

        assertThrows(PriorityNotFoundException.class, () -> ticketService.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("CallNextTicket should use STRICT strategy by default and return ticket")
    void callNextTicketShouldUseStrictAndReturnTicket() {
        var nextTicket = Ticket.builder()
                .id(UUID.randomUUID())
                .status(TicketStatus.WAITING)
                .build();
                new Ticket();

        when(repository.findNextStrict()).thenReturn(Optional.of(nextTicket));
        when(repository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Ticket.class))).thenReturn(mock(TicketResponse.class));

        ticketService.callNextTicket();

        verify(repository).findNextStrict();
        verify(repository, never()).findNextFifo();

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(repository).save(ticketCaptor.capture());

        Ticket captured = ticketCaptor.getValue();
        assertEquals(TicketStatus.IN_PROGRESS, captured.getStatus());
        assertNotNull(captured.getCalledAt());
        assertEquals(1, captured.getCallCount());
    }

    @Test
    @DisplayName("CallNextTicket should use FIFO strategy when configured")
    void callNextTicketShouldUseFifoWhenConfigured() {
        ReflectionTestUtils.setField(ticketService, "strategy", "FIFO");

        Ticket nextTicket = new Ticket();
        when(repository.findNextFifo()).thenReturn(Optional.of(nextTicket));
        when(repository.save(any())).thenReturn(nextTicket);

        ticketService.callNextTicket();

        verify(repository).findNextFifo();
        verify(repository, never()).findNextStrict();
    }

    @Test
    @DisplayName("CallNextTicket should throw BusinessException when queue is empty")
    void callNextTicket_ShouldThrowException_WhenQueueIsEmpty() {
        when(repository.findNextStrict()).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> ticketService.callNextTicket());
        assertEquals("No one on queue.", exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("UpdateStatus should update ticket when status is valid")
    void updateStatusShouldUpdateWhenTicketIsOpen() {
        UUID ticketId = UUID.randomUUID();
        var ticket = Ticket.builder()
                .id(ticketId)
                .status(TicketStatus.IN_PROGRESS)
                .build();

        var request = TicketUpdateRequest.builder()
                .status(TicketStatus.CLOSED)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(repository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponse(any(Ticket.class))).thenReturn(mock(TicketResponse.class));

        ticketService.updateStatus(ticketId, request);

        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
        verify(repository).save(ticket);
    }

    @Test
    @DisplayName("UpdateStatus should set closedAt when status is CLOSED")
    void updateStatusShouldSetClosedAtWhenStatusIsClosed() {
        UUID ticketId = UUID.randomUUID();
        var ticket = Ticket.builder()
                .id(ticketId)
                .status(TicketStatus.IN_PROGRESS)
                .build();

        var request = TicketUpdateRequest.builder()
                .status(TicketStatus.CLOSED)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(repository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        ticketService.updateStatus(ticketId, request);

        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
        assertNotNull(ticket.getClosedAt());
    }

    @Test
    @DisplayName("UpdateStatus should throw BusinessException when ticket is already CLOSED")
    void updateStatusShouldThrowExceptionWhenTicketIsAlreadyClosed() {
        UUID ticketId = UUID.randomUUID();

        var ticket = Ticket.builder()
                .status(TicketStatus.CLOSED)
                .build();

        var request = TicketUpdateRequest.builder()
                .status(TicketStatus.IN_PROGRESS)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class, () -> ticketService.updateStatus(ticketId, request));
        assertEquals("This ticket has been closed", ex.getMessage());

        verify(repository, never()).save(any());
    }
}
