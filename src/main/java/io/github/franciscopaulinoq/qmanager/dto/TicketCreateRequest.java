package io.github.franciscopaulinoq.qmanager.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TicketCreateRequest(
        @NotNull(message = "categoryId is required")
        UUID categoryId,

        @NotNull(message = "priorityId is required")
        UUID priorityId
) {
}
