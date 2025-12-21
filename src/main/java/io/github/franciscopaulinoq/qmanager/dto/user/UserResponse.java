package io.github.franciscopaulinoq.qmanager.dto.user;

import io.github.franciscopaulinoq.qmanager.dto.role.RoleResponse;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        boolean active,
        Set<RoleResponse> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
