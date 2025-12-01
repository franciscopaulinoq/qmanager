package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.priority.PriorityRequest;
import io.github.franciscopaulinoq.qmanager.dto.priority.PriorityResponse;
import io.github.franciscopaulinoq.qmanager.service.PriorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/priorities")
@RequiredArgsConstructor
@Tag(name = "Priorities", description = "Endpoints para gerenciar prioridades")
public class PriorityController {
    private final PriorityService service;

    @GetMapping
    @Operation(summary = "Listar prioridades ativas", description = "Retorna todas as prioridades cujo campo active é true")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de prioridades retornada com sucesso")
    })
    public ResponseEntity<List<PriorityResponse>> listAll() {
        return ResponseEntity.ok(service.findAllByActiveTrue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter prioridade por id", description = "Retorna uma prioridade por UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prioridade encontrada"),
        @ApiResponse(responseCode = "404", description = "Prioridade não encontrada")
    })
    public ResponseEntity<PriorityResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar prioridade", description = "Cria uma nova prioridade")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prioridade criada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<PriorityResponse> create(@RequestBody @Valid PriorityRequest request) {
        var response = service.create(request);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar prioridade", description = "Atualiza uma prioridade existente por UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prioridade atualizada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "404", description = "Prioridade não encontrada")
    })
    public ResponseEntity<PriorityResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid PriorityRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover prioridade", description = "Desativa/Remove uma prioridade por UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Prioridade removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Prioridade não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
