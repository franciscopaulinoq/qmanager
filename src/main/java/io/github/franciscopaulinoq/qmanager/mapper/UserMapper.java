package io.github.franciscopaulinoq.qmanager.mapper;

import io.github.franciscopaulinoq.qmanager.dto.role.RoleResponse;
import io.github.franciscopaulinoq.qmanager.dto.user.UserCreateRequest;
import io.github.franciscopaulinoq.qmanager.dto.user.UserListResponse;
import io.github.franciscopaulinoq.qmanager.dto.user.UserResponse;
import io.github.franciscopaulinoq.qmanager.model.Role;
import io.github.franciscopaulinoq.qmanager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);

    UserListResponse toListResponse(User user);
}
