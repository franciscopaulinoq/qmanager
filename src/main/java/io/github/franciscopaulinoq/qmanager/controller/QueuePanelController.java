package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.QueuePanelResponse;
import io.github.franciscopaulinoq.qmanager.service.QueuePainelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queue-panel")
@RequiredArgsConstructor
public class QueuePanelController {
    private final QueuePainelService service;

    @GetMapping
    public ResponseEntity<QueuePanelResponse> getMonitor() {
        return ResponseEntity.ok(service.getTicketMonitor());
    }
}
