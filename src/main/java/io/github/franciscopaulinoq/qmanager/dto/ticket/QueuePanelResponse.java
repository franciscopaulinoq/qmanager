package io.github.franciscopaulinoq.qmanager.dto.ticket;

import lombok.Builder;

import java.util.List;

@Builder
public record QueuePanelResponse(
        TicketResponse current,
        List<TicketResponse> history
) {
}
