package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.priority.PriorityRequest;
import io.github.franciscopaulinoq.qmanager.dto.priority.PriorityResponse;
import io.github.franciscopaulinoq.qmanager.exception.PriorityAlreadyExistsException;
import io.github.franciscopaulinoq.qmanager.exception.PriorityNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.PriorityMapper;
import io.github.franciscopaulinoq.qmanager.model.Priority;
import io.github.franciscopaulinoq.qmanager.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriorityService {
    private final PriorityRepository repository;
    private final PriorityMapper mapper;

    private Priority getPriorityOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PriorityNotFoundException("Priority not found: " + id));
    }

    public List<PriorityResponse> findAllByActiveTrue() {
        return mapper.toResponse(repository.findAllByActiveTrue());
    }

    public PriorityResponse findById(UUID id) {
        return mapper.toResponse(getPriorityOrThrow(id));
    }

    public PriorityResponse create(PriorityRequest request) {
        if (repository.existsByPrefix(request.prefix())) {
            throw new PriorityAlreadyExistsException("Priority already exists: " + request.prefix());
        }

        Priority priority = mapper.toEntity(request);

        priority.setActive(true);

        Priority saved = repository.save(priority);
        return mapper.toResponse(saved);
    }

    public PriorityResponse update(UUID id, PriorityRequest request) {
        Priority priority = getPriorityOrThrow(id);

        if (repository.existsByPrefix(request.prefix()) && !priority.getPrefix().equals(request.prefix())) {
            throw new PriorityAlreadyExistsException("Priority already exists: " + request.prefix());
        }

        mapper.updateEntityFromRequest(request, priority);

        return mapper.toResponse(repository.save(priority));
    }

    public void delete(UUID id) {
        Priority priority = getPriorityOrThrow(id);
        priority.setActive(false);

        repository.save(priority);
    }
}
