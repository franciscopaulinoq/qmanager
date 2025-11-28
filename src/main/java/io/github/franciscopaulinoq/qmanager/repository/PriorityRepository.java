package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PriorityRepository extends JpaRepository<Priority, UUID> {
    List<Priority> findAllByActiveTrue();
    boolean existsByPrefix(String prefix);
}
