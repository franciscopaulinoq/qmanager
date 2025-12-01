package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketResponse;
import io.github.franciscopaulinoq.qmanager.dto.ticket.TicketUpdateRequest;
import io.github.franciscopaulinoq.qmanager.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tickets", description = "Endpoints para gerenciar tickets/chamados")
public class TicketController {
    @Value("${qmanager.web.max-page-size}")
    private int maxPageSize;

    private final TicketService service;

    @PostMapping
    @Operation(summary = "Criar ticket", description = "Cria um novo ticket/chamado")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ticket criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<TicketResponse> create(@RequestBody @Valid TicketCreateRequest request) {
        var response = service.create(request);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar tickets com paginação", description = "Retorna uma lista paginada de todos os tickets")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tickets retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Página inválida")
    })
    public ResponseEntity<Page<TicketResponse>> listAllTickets(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, maxPageSize);
        return ResponseEntity.ok(service.listAllTickets(pageable));
    }

    @PostMapping("/next")
    @Operation(summary = "Chamar próximo ticket", description = "Chama o próximo ticket da fila para atendimento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Próximo ticket retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nenhum ticket disponível na fila")
    })
    public ResponseEntity<TicketResponse> next() {
        return ResponseEntity.ok(service.callNextTicket());
    }

    @PostMapping("/{id}/hold")
    @Operation(summary = "Colocar ticket em espera", description = "Move um ticket para status pendente/espera")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket movido para espera com sucesso"),
        @ApiResponse(responseCode = "404", description = "Ticket não encontrado"),
        @ApiResponse(responseCode = "400", description = "Transição de estado inválida")
    })
    public ResponseEntity<TicketResponse> moveToPending(@PathVariable UUID id) {
        return ResponseEntity.ok(service.moveToPending(id));
    }

    @PostMapping("/{id}/reactivate")
    @Operation(summary = "Reativar ticket", description = "Reativa um ticket que estava em espera, colocando-o novamente na fila")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket reativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Ticket não encontrado"),
        @ApiResponse(responseCode = "400", description = "Transição de estado inválida")
    })
    public ResponseEntity<TicketResponse> reactivateTicket(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "true") boolean urgent
    ) {
        return ResponseEntity.ok(service.reactivateTicket(id, urgent));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar status do ticket", description = "Atualiza o status de um ticket existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
    })
    public ResponseEntity<TicketResponse> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid TicketUpdateRequest request
    ) {
        return ResponseEntity.ok(service.updateStatus(id, request));
    }
}
