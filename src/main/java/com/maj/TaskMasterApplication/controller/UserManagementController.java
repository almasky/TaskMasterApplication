package com.maj.TaskMasterApplication.controller;

import com.maj.TaskMasterApplication.dto.UserResponseDto;
import com.maj.TaskMasterApplication.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")   // Base path for user management by admin
@PreAuthorize("hasRole('ADMIN')")     // All methods in this controller require ADMIN role by default
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /**
     * Assigns a role to a user. The roleName should be one of the defined Roles (e.g., USER, ADMIN).
     */
    @PutMapping("/{userId}/assign-role/{roleName}")
    public ResponseEntity<UserResponseDto> assignUserRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        UserResponseDto updatedUser = userManagementService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Gets a list of all users in the system.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userManagementService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Gets a specific user by their ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        UserResponseDto user = userManagementService.findUserById(userId);
        return ResponseEntity.ok(user);
    }
}