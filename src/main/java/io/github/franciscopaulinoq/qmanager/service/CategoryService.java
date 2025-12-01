package io.github.franciscopaulinoq.qmanager.service;

import io.github.franciscopaulinoq.qmanager.dto.category.CategoryRequest;
import io.github.franciscopaulinoq.qmanager.dto.category.CategoryResponse;
import io.github.franciscopaulinoq.qmanager.exception.CategoryAlreadyExistsException;
import io.github.franciscopaulinoq.qmanager.exception.CategoryNotFoundException;
import io.github.franciscopaulinoq.qmanager.mapper.CategoryMapper;
import io.github.franciscopaulinoq.qmanager.model.Category;
import io.github.franciscopaulinoq.qmanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    private Category getCategoryOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + id));
    }

    public List<CategoryResponse> findAllByActiveTrue() {
        return mapper.toResponse(repository.findAllByActiveTrue());
    }

    public CategoryResponse findById(UUID id) {
        return mapper.toResponse(getCategoryOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByPrefix(request.prefix())) {
            throw new CategoryAlreadyExistsException("Category prefix already exists: " + request.prefix());
        }

        Category entity = mapper.toEntity(request);

        entity.setActive(true);

        Category saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = getCategoryOrThrow(id);

        if (repository.existsByPrefix(request.prefix()) && !category.getPrefix().equals(request.prefix())) {
            throw new CategoryAlreadyExistsException("Category prefix already exists: " + request.prefix());
        }

        mapper.updateEntityFromRequest(request, category);

        return mapper.toResponse(repository.save(category));
    }

    public void delete(UUID id) {
        Category category = getCategoryOrThrow(id);

        category.setActive(false);
        repository.save(category);
    }
}
