package io.github.franciscopaulinoq.qmanager.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserCreateRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotBlank(message = "email is required")
        @Email
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be eight or more characters")
        String password,

        @NotBlank(message = "confirmPassword is required")
        String confirmPassword,

        Set<UUID> rolesId
) {
    @AssertTrue(message = "Passwords do not match")
    @JsonIgnore
    public boolean isPasswordMatching() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }
}
