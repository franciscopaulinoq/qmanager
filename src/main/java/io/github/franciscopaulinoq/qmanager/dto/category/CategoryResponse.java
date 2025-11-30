package io.github.franciscopaulinoq.qmanager.dto.category;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record CategoryResponse(
        UUID id,
        String name,
        String prefix,
        boolean active,
        OffsetDateTime createdAt
) {
}
