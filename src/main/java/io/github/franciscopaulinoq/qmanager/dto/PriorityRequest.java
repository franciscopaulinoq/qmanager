package io.github.franciscopaulinoq.qmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PriorityRequest(
        @NotBlank(message = "The prefix is mandatory")
        @Size(min = 1, max = 1, message = "The prefix must be one character")
        String prefix,

        @NotBlank(message = "The weight is mandatory")
        @Size(min = 1, message = "The weight must be one or more")
        int weight,

        @NotBlank(message = "The name is mandatory")
        String name
) {
}
