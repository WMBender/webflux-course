package br.com.weslei.bender.webfluxcourse.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank(message = "Must not be null or empty")
    @Size(min = 3, max = 50, message = "Must be between 3 and 50 characters")
    String name,

    @Email(message = "Invalid email")
    @NotBlank(message = "Must not be null or empty")
    String email,
    @NotBlank(message = "Must not be null or empty")
    @Size(min = 3, max = 50, message = "Must be between 3 and 50 characters")
    String password
) { }
