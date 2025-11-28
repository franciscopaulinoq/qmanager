package io.github.franciscopaulinoq.qmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_sequences")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "sequence_prefix", nullable = false, unique = true, length = 10)
    private String sequencePrefix;

    @Column(name = "current_number", nullable = false)
    private int currentNumber = 0;

    @Column(name = "last_reset_at")
    private OffsetDateTime lastResetAt;
}
