package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.TicketSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TicketSequenceRepository extends JpaRepository<TicketSequence, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketSequence t WHERE t.sequencePrefix = :prefix")
    Optional<TicketSequence> findBySequencePrefixForUpdate(@Param("prefix") String prefix);

    @Modifying
    @Query(value = "INSERT INTO ticket_sequences (id, sequence_prefix, current_number, last_reset_at) " +
            "VALUES (gen_random_uuid(), :prefix, 0, :now) " +
            "ON CONFLICT (sequence_prefix) DO NOTHING", nativeQuery = true)
    void insertIgnore(@Param("prefix") String prefix, @Param("now") OffsetDateTime now);
}
