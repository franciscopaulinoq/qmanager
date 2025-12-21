package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.role.RoleRequest;
import io.github.franciscopaulinoq.qmanager.dto.role.RoleResponse;
import io.github.franciscopaulinoq.qmanager.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para gerenciar roles (funções)")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Listar roles", description = "Retorna uma lista de todos as roles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de roles retornada com sucesso")
    })
    public ResponseEntity<List<RoleResponse>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter role por id", description = "Retorna uma role por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role encontrada"),
            @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    public ResponseEntity<RoleResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar role", description = "Cria uma nova role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role criada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<RoleResponse> create(@RequestBody @Valid RoleRequest roleRequest) {
        var role = roleService.create(roleRequest);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(role.id())
                .toUri();

        return ResponseEntity.created(uri).body(role);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar role", description = "Atualiza uma role existente por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role atualizado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    public ResponseEntity<RoleResponse> update(@PathVariable UUID id, @RequestBody @Valid RoleRequest roleRequest) {
        return ResponseEntity.ok(roleService.update(id, roleRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover role", description = "Desativa/Remove uma role por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
