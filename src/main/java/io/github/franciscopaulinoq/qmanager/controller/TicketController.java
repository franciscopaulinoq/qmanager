package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.TicketCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.TicketMonitorResponse;
import io.github.franciscopaulinoq.qmanager.dto.TicketResponse;
import io.github.franciscopaulinoq.qmanager.dto.TicketUpdateRequest;
import io.github.franciscopaulinoq.qmanager.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    @Value("${qmanager.web.max-page-size}")
    private int maxPageSize;

    private final TicketService service;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@RequestBody @Valid TicketCreateRequest request) {
        var response = service.create(request);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> listAllTickets(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, maxPageSize);
        return ResponseEntity.ok(service.listAllTickets(pageable));
    }

    @GetMapping("/monitor")
    public ResponseEntity<TicketMonitorResponse> getMonitor() {
        return ResponseEntity.ok(service.getTicketMonitor());
    }

    @PostMapping("/next")
    public ResponseEntity<TicketResponse> next() {
        return ResponseEntity.ok(service.callNextTicket());
    }

    @PostMapping("/tickets/{id}/calls")
    public ResponseEntity<TicketResponse> callAgain(@PathVariable UUID id) {
        return ResponseEntity.ok(service.callAgain(id));
    }

    @PostMapping("/tickets/{id}/hold")
    public ResponseEntity<TicketResponse> moveToPending(@PathVariable UUID id) {
        return ResponseEntity.ok(service.moveToPending(id));
    }

    @PostMapping("/tickets/{id}/reactivate")
    public ResponseEntity<TicketResponse> reactivateTicket(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "true") boolean urgent
    ) {
        return ResponseEntity.ok(service.reactivateTicket(id, urgent));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TicketResponse> update(@PathVariable("id") UUID id, TicketUpdateRequest request) {
        return ResponseEntity.ok(service.updateStatus(id, request));
    }
}
