package io.github.franciscopaulinoq.qmanager.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CategoryRequest(
        @NotBlank(message = "The prefix is mandatory")
        @Size(min = 1, max = 1, message = "The prefix must be one character")
        String prefix,

        @NotBlank(message = "The name is mandatory")
        @Size(max = 100)
        String name
) {
}
