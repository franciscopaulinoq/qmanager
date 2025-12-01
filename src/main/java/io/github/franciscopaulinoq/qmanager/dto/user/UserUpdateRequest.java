package io.github.franciscopaulinoq.qmanager.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName
) {
}
