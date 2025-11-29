package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.TicketCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.TicketResponse;
import io.github.franciscopaulinoq.qmanager.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public Page<TicketResponse> listAllTickets(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, maxPageSize);
        return service.listAllTickets(pageable);
    }
}
