package io.github.franciscopaulinoq.qmanager.dto.ticket;

import io.github.franciscopaulinoq.qmanager.model.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TicketUpdateRequest(
        @NotNull(message = "status is required")
        TicketStatus status
) {
}
