package io.github.franciscopaulinoq.qmanager.dto.ticket;

import java.util.List;

public record QueuePanelResponse(
        TicketResponse current,
        List<TicketResponse> history
) {
}
