package io.github.franciscopaulinoq.qmanager.dto;

import java.util.List;

public record TicketMonitorResponse(
        TicketResponse current,
        List<TicketResponse> history
) {
}
