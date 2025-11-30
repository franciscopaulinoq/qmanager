package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.QueuePanelResponse;
import io.github.franciscopaulinoq.qmanager.dto.TicketResponse;
import io.github.franciscopaulinoq.qmanager.mapper.TicketMapper;
import io.github.franciscopaulinoq.qmanager.model.Ticket;
import io.github.franciscopaulinoq.qmanager.model.TicketStatus;
import io.github.franciscopaulinoq.qmanager.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueuePainelService {
    private final TicketRepository repository;
    private final TicketMapper mapper;

    public QueuePanelResponse getTicketMonitor() {
        List<Ticket> recentHistory = repository.findRecentHistory(LocalDate.now());

        TicketResponse current = null;
        List<TicketResponse> historyResponse;

        if (!recentHistory.isEmpty()) {
            Ticket latest = recentHistory.getFirst();

            if (latest.getStatus() == TicketStatus.IN_PROGRESS) {
                current = mapper.toResponse(latest);
                historyResponse = mapper.toResponse(recentHistory.subList(1, recentHistory.size()));
            } else {
                historyResponse = mapper.toResponse(recentHistory);
            }
        } else {
            historyResponse = List.of();
        }

        return new QueuePanelResponse(current, historyResponse);
    }
}
