package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.model.TicketSequence;
import io.github.franciscopaulinoq.qmanager.repository.TicketSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TicketSequenceService {
    private final TicketSequenceRepository repository;

    @Transactional
    public int getNextSequence(String categoryPrefix, String priorityPrefix) {
        String sequencePrefix = categoryPrefix + priorityPrefix;

        repository.insertIgnore(sequencePrefix, OffsetDateTime.now());

        TicketSequence sequence = repository.findBySequencePrefixForUpdate(sequencePrefix)
                .orElseThrow(() -> new IllegalStateException("Sequence prefix not found"));

        LocalDate today = LocalDate.now();
        if (!sequence.getLastResetAt().toLocalDate().equals(today)) {
            sequence.setCurrentNumber(0);
            sequence.setLastResetAt(OffsetDateTime.now());
        }

        sequence.setCurrentNumber(sequence.getCurrentNumber() + 1);
        repository.save(sequence);

        return sequence.getCurrentNumber();
    }
}
