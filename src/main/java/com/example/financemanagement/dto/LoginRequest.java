package com.example.financemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username cannot be blank")
    @Email(message = "Username must be a valid email address")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
} 