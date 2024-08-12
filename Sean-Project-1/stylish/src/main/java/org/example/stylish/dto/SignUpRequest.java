package org.example.stylish.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be between 1 and 50 characters.")
    String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid.")
    String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters")
    String password;
}
