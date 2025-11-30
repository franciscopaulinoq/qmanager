package io.github.franciscopaulinoq.qmanager.dto.priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record PriorityRequest(
        @NotBlank(message = "The prefix is mandatory")
        @Size(min = 1, max = 1, message = "The prefix must be one character")
        String prefix,

        @NotNull(message = "The weight is mandatory")
        @Min(value = 1, message = "The weight must be one or more")
        int weight,

        @NotBlank(message = "The name is mandatory")
        String name
) {
}
