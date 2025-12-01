package io.github.franciscopaulinoq.qmanager.dto.user;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
