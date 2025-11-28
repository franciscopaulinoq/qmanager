package io.github.franciscopaulinoq.qmanager.mapper;

import io.github.franciscopaulinoq.qmanager.dto.PriorityRequest;
import io.github.franciscopaulinoq.qmanager.dto.PriorityResponse;
import io.github.franciscopaulinoq.qmanager.model.Priority;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PriorityMapper {
    PriorityResponse toResponse(Priority priority);

    Priority toEntity(PriorityRequest request);

    List<PriorityResponse> toResponse(List<Priority> priorities);

    void updateEntityFromRequest(PriorityRequest request, @MappingTarget Priority entity);
}
