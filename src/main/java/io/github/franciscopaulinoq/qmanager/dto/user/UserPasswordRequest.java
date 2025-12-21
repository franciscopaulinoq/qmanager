package io.github.franciscopaulinoq.qmanager.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserPasswordRequest(
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be eight or more characters")
        String password,

        @NotBlank(message = "confirmPassword is required")
        String confirmPassword
) {
    @AssertTrue(message = "Passwords do not match")
    @JsonIgnore
    public boolean isPasswordMatching() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }
}
