package io.github.franciscopaulinoq.qmanager.mapper;

import io.github.franciscopaulinoq.qmanager.dto.TicketResponse;
import io.github.franciscopaulinoq.qmanager.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class, PriorityMapper.class}
)
public interface TicketMapper {
    TicketResponse toResponse(Ticket ticket);

    List<TicketResponse> toResponse(List<Ticket> tickets);
}
