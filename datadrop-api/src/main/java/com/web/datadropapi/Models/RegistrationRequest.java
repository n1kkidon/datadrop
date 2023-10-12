package com.web.datadropapi.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}

