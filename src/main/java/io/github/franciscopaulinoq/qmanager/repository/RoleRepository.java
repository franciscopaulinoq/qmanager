package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByIdAndActiveTrue(UUID id);
    List<Role> findAllByActiveTrue();
    List<Role> findAllByIdInAndActiveTrue(Set<UUID> ids);
}
