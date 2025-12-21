package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.user.UserCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserListResponse;
import io.github.franciscopaulinoq.qmanager.dto.user.UserPasswordRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserResponse;
import io.github.franciscopaulinoq.qmanager.dto.user.UserUpdateRequest;
import io.github.franciscopaulinoq.qmanager.exception.ResourceAlreadyExistsException;
import io.github.franciscopaulinoq.qmanager.exception.ResourceNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.UserMapper;
import io.github.franciscopaulinoq.qmanager.model.Role;
import io.github.franciscopaulinoq.qmanager.model.User;
import io.github.franciscopaulinoq.qmanager.repository.RoleRepository;
import io.github.franciscopaulinoq.qmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository repository;
    private final UserMapper mapper;

    private User getUserOrThrow(UUID id) {
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse findById(UUID id) {
        return mapper.toResponse(getUserOrThrow(id));
    }

    public Page<UserListResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toListResponse);
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }

        var user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (request.rolesId() != null && !request.rolesId().isEmpty()) {
            List<Role> foundRoles = roleRepository.findAllByIdInAndActiveTrue(request.rolesId());

            if (foundRoles.size() != request.rolesId().size()) {
                throw new ResourceNotFoundException("One or more roles not found or inactive");
            }

            user.setRoles(new HashSet<>(foundRoles));
        }

        return mapper.toResponse(repository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        var user = getUserOrThrow(id);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        if (request.rolesId() != null && !request.rolesId().isEmpty()) {
            List<Role> foundRoles = roleRepository.findAllByIdInAndActiveTrue(request.rolesId());

            if (foundRoles.size() != request.rolesId().size()) {
                throw new ResourceNotFoundException("One or more roles not found or inactive");
            }

            user.setRoles(new HashSet<>(foundRoles));
        }

        return mapper.toResponse(repository.save(user));
    }

    @Transactional
    public void updatePassword(UUID id, UserPasswordRequest request) {
        var user = getUserOrThrow(id);

        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);

        repository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        var user = getUserOrThrow(id);

        user.setActive(false);
        repository.save(user);
    }
}
