package com.maj.TaskMasterApplication.service;

import com.maj.TaskMasterApplication.dto.AuthResponseDto;
import com.maj.TaskMasterApplication.dto.LoginRequestDto;
import com.maj.TaskMasterApplication.dto.SignUpRequestDto;
import com.maj.TaskMasterApplication.dto.UserResponseDto;
// import com.maj.TaskMasterApplication.model.User; // If needed for internal methods returning User entity

public interface UserService {

    /**
     * Registers a new user.
     * @param signUpRequestDto DTO containing user registration details.
     * @return AuthResponseDto containing JWT token and user details.
     */
    AuthResponseDto registerUser(SignUpRequestDto signUpRequestDto);

    /**
     * Authenticates a user and provides a JWT token.
     * @param loginRequestDto DTO containing login credentials.
     * @return AuthResponseDto containing JWT token and user details.
     */
    AuthResponseDto loginUser(LoginRequestDto loginRequestDto);

    /**
     * Finds a user by their ID.
     * @param userId The ID of the user.
     * @return UserResponseDto for the found user.
     */
    UserResponseDto getUserById(Long userId); // Primarily for internal use or admin scenarios

    // Potentially more methods for user management by admins:
    // List<UserResponseDto> getAllUsers();
    // UserResponseDto updateUserRole(Long userId, Roles newRole);
}