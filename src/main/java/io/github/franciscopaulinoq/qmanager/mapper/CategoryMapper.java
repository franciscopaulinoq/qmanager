package io.github.franciscopaulinoq.qmanager.mapper;

import io.github.franciscopaulinoq.qmanager.dto.CategoryRequest;
import io.github.franciscopaulinoq.qmanager.dto.CategoryResponse;
import io.github.franciscopaulinoq.qmanager.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest request);

    List<CategoryResponse> toResponse(List<Category> categories);

    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category entity);
}
