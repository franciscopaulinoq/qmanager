package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
