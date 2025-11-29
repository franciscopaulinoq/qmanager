package io.github.franciscopaulinoq.qmanager.repository;

import io.github.franciscopaulinoq.qmanager.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

}
