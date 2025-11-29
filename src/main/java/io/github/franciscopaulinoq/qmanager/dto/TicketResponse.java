package io.github.franciscopaulinoq.qmanager.dto;

import io.github.franciscopaulinoq.qmanager.model.Ticket;
import io.github.franciscopaulinoq.qmanager.model.TicketStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    String code,
    TicketStatus status,
    LocalDate issueDate,
    CategoryResponse category,
    PriorityResponse priority,
    OffsetDateTime createdAt,
    OffsetDateTime calledAt,
    OffsetDateTime closedAt
) {
}
