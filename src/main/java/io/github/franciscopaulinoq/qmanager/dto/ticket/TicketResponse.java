package io.github.franciscopaulinoq.qmanager.dto.ticket;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    String code,
    String status,
    String categoryName,
    String priorityName,
    Integer callCount,
    OffsetDateTime createdAt,
    OffsetDateTime calledAt,
    OffsetDateTime closedAt
) {
}
