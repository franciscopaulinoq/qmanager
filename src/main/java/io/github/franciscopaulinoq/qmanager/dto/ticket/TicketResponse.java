package io.github.franciscopaulinoq.qmanager.dto.ticket;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record TicketResponse(
        UUID id,
        String code,
        String status,
        String categoryName,
        String priorityName,
        Integer callCount,
        OffsetDateTime startedAt,
        OffsetDateTime calledAt,
        OffsetDateTime closedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
