package com.maj.TaskMasterApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Username or email is required")
    private String loginIdentifier; // Can be username or email

    @NotBlank(message = "Password is required")
    private String password;
}