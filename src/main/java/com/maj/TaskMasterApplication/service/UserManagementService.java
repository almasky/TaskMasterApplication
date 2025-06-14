package com.maj.TaskMasterApplication.service;

import com.maj.TaskMasterApplication.dto.UserResponseDto;
import java.util.List;

public interface UserManagementService {

    /**
     * Assigns a specific role to a user.
     *
     * @param userId The ID of the user whose role is to be changed.
     * @param roleName The name of the role to assign (e.g., "ADMIN", "USER").
     * @return UserResponseDto of the updated user.
     * @throws com.maj.TaskMasterApplication.exception.ResourceNotFoundException if user not found.
     * @throws com.maj.TaskMasterApplication.exception.BadRequestException if roleName is invalid.
     */
    UserResponseDto assignRoleToUser(Long userId, String roleName);

    /**
     * Retrieves a list of all users.
     *
     * @return A list of UserResponseDto for all users.
     */
    List<UserResponseDto> findAllUsers();

    /**
     * Retrieves a specific user by their ID.
     * (This might be redundant if you already have a similar method in UserService,
     * but can be useful to keep user management concerns together)
     * @param userId The ID of the user to retrieve.
     * @return UserResponseDto of the found user.
     * @throws com.maj.TaskMasterApplication.exception.ResourceNotFoundException if user not found.
     */
    UserResponseDto findUserById(Long userId);
}