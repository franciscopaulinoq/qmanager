package io.github.franciscopaulinoq.qmanager.dto.user;

import io.github.franciscopaulinoq.qmanager.dto.role.RoleResponse;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserListResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
