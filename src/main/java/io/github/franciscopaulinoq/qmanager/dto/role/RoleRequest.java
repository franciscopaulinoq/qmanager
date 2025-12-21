package io.github.franciscopaulinoq.qmanager.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RoleRequest(
        @NotBlank(message = "The name is mandatory")
        String name,

        String description
) {
}
