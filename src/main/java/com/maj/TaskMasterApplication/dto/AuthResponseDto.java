package com.maj.TaskMasterApplication.dto;

import com.maj.TaskMasterApplication.model.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private Roles role;

    public AuthResponseDto(String accessToken, Long userId, String username, Roles role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}