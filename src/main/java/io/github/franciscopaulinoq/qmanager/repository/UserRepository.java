package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndActiveTrue(UUID id);
}
