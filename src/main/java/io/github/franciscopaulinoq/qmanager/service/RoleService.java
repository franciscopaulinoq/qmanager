package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.role.RoleRequest;
import io.github.franciscopaulinoq.qmanager.dto.role.RoleResponse;
import io.github.franciscopaulinoq.qmanager.exception.ResourceNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.RoleMapper;
import io.github.franciscopaulinoq.qmanager.model.Role;
import io.github.franciscopaulinoq.qmanager.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;

    private Role getRoleOrThrow(UUID id) {
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    public RoleResponse findById(UUID id) {
        return mapper.toResponse(getRoleOrThrow(id));
    }

    public List<RoleResponse> findAll() {
        return repository.findAllByActiveTrue().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public RoleResponse create(RoleRequest roleRequest) {
        var role = mapper.toEntity(roleRequest);

        return mapper.toResponse(repository.save(role));
    }

    public RoleResponse update(UUID id, RoleRequest roleRequest) {
        var role = getRoleOrThrow(id);

        role.setName(roleRequest.name());
        role.setDescription(roleRequest.description());

        return mapper.toResponse(repository.save(role));
    }

    public void delete(UUID id) {
        var role = getRoleOrThrow(id);

        role.setActive(false);
        repository.save(role);
    }
}
