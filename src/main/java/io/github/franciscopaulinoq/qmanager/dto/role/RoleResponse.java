package io.github.franciscopaulinoq.qmanager.dto.role;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RoleResponse(
        UUID id,
        String name,
        String description
) {
}
