package io.github.franciscopaulinoq.qmanager.dto.priority;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record PriorityResponse(
        UUID id,
        String prefix,
        int weight,
        String name,
        boolean active,
        OffsetDateTime createdAt
) {
}
