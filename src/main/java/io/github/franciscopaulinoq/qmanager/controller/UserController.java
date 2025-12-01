package io.github.franciscopaulinoq.qmanager.controller;

import io.github.franciscopaulinoq.qmanager.dto.user.UserCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserPasswordRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserResponse;
import io.github.franciscopaulinoq.qmanager.dto.user.UserUpdateRequest;
import io.github.franciscopaulinoq.qmanager.service.UserService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints para gerenciar usuários")
public class UserController {
    @Value("${qmanager.web.max-page-size}")
    private int maxPageSize;

    private final UserService service;

    @GetMapping
    @Operation(summary = "Listar usuários com paginação", description = "Retorna uma lista paginada de todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Página inválida")
    })
    public ResponseEntity<Page<UserResponse>> findAll(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, maxPageSize);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por id", description = "Retorna um usuário por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "429", description = "E-mail já registrado")
    })
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {
        var user = service.create(request);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.id())
                .toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza um usuário existente por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover usuário", description = "Desativa/Remove um usuário por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password-reset")
    @Operation(summary = "Atualizar senha do usuário", description = "Atualiza a senha de um usuário existente por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @RequestBody @Valid UserPasswordRequest request) {
        service.updatePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
