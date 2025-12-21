package io.github.franciscopaulinoq.qmanager.mapper;

import io.github.franciscopaulinoq.qmanager.dto.role.RoleRequest;
import io.github.franciscopaulinoq.qmanager.dto.role.RoleResponse;
import io.github.franciscopaulinoq.qmanager.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {
    RoleResponse toResponse(Role role);

    Role toEntity(RoleRequest request);

    Set<RoleResponse> toResponse(Set<Role> roles);
}
