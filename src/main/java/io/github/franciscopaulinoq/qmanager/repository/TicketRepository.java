package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    @Query("""
                SELECT t FROM Ticket t JOIN t.priority p
                WHERE t.status = 'WAITING' AND t.issueDate = CURRENT_DATE
                ORDER BY p.weight DESC, t.createdAt ASC
                LIMIT 1
            """)
    Optional<Ticket> findNextStrict();

    @Query("""
                SELECT t FROM Ticket t
                WHERE t.status = 'WAITING' AND t.issueDate = CURRENT_DATE
                ORDER BY t.createdAt ASC
                LIMIT 1
            """)
    Optional<Ticket> findNextFifo();
}
