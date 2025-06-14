package com.maj.TaskMasterApplication.impl;

import com.maj.TaskMasterApplication.dto.UserResponseDto;
import com.maj.TaskMasterApplication.exception.BadRequestException;
import com.maj.TaskMasterApplication.exception.ResourceNotFoundException;
import com.maj.TaskMasterApplication.model.Roles;
import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.repository.UserRepository;
import com.maj.TaskMasterApplication.service.UserManagementService;
import com.maj.TaskMasterApplication.util.DtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // All public methods will be transactional by default
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserManagementServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Attempted to assign role to non-existent user with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        // Prevent admin from accidentally de-admining the last admin or themselves without more complex logic
        // For now, we'll keep it simple. A more robust system might have checks here.
        // Example: if (user.getRole() == Roles.ADMIN && "USER".equals(roleName.toUpperCase()) && countAdmins() <= 1) {
        // throw new BadRequestException("Cannot remove the last admin's privileges.");
        // }

        try {
            Roles newRole = Roles.valueOf(roleName.toUpperCase()); // Convert string to enum, case-insensitive for input
            if (user.getRole() == newRole) {
                logger.info("User {} already has role {}. No change made.", user.getUsername(), newRole);
                // Optionally throw an exception or just return current state
                // throw new BadRequestException("User already has the role: " + newRole);
            } else {
                user.setRole(newRole);
                logger.info("Assigned role {} to user {}", newRole, user.getUsername());
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Attempted to assign invalid role '{}' to user {}", roleName, user.getUsername());
            throw new BadRequestException("Invalid role: " + roleName + ". Valid roles are " +
                    java.util.Arrays.stream(Roles.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")) + ".");
        }

        User updatedUser = userRepository.save(user);
        return DtoMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        logger.debug("Fetching all users for admin.");
        return userRepository.findAll().stream()
                .map(DtoMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Admin attempted to find non-existent user with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
        logger.debug("Admin fetching user by ID: {}", userId);
        return DtoMapper.toUserResponseDto(user);
    }

    // Helper method example for a more robust check (not directly used in assignRoleToUser yet)
    // private long countAdmins() {
    // return userRepository.countByRole(Roles.ADMIN); // Requires a countByRole method in UserRepository
    // }
}