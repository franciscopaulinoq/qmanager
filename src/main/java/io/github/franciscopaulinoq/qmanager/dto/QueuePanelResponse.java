package io.github.franciscopaulinoq.qmanager.dto;

import java.util.List;

public record QueuePanelResponse(
        TicketResponse current,
        List<TicketResponse> history
) {
}
