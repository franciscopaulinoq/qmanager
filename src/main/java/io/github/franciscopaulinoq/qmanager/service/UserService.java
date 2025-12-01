package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.user.UserCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserPasswordRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserResponse;
import io.github.franciscopaulinoq.qmanager.dto.user.UserUpdateRequest;
import io.github.franciscopaulinoq.qmanager.exception.ResourceAlreadyExistsException;
import io.github.franciscopaulinoq.qmanager.exception.ResourceNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.UserMapper;
import io.github.franciscopaulinoq.qmanager.model.User;
import io.github.franciscopaulinoq.qmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final UserMapper mapper;

    private User getUserOrThrow(UUID id) {
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse findById(UUID id) {
        return mapper.toResponse(getUserOrThrow(id));
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public UserResponse create(UserCreateRequest request) {
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }

        var user = mapper.toEntity(request);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return mapper.toResponse(repository.save(user));
    }

    public UserResponse update(UUID id, UserUpdateRequest request) {
        var user = getUserOrThrow(id);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        return mapper.toResponse(repository.save(user));
    }

    public void updatePassword(UUID id, UserPasswordRequest request) {
        var user = getUserOrThrow(id);

        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);

        repository.save(user);
    }

    public void delete(UUID id) {
        var user = getUserOrThrow(id);

        user.setActive(false);
        repository.save(user);
    }
}
