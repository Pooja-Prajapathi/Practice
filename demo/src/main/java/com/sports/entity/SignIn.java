package com.sports.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignIn {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}