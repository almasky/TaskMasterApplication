package com.maj.TaskMasterApplication.controller;

import com.maj.TaskMasterApplication.dto.TaskRequestDto;
import com.maj.TaskMasterApplication.dto.TaskResponseDto;
import com.maj.TaskMasterApplication.model.User; // Your User entity that implements UserDetails
import com.maj.TaskMasterApplication.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // --- Helper method to get current user's ID ---
    // Not strictly necessary if User object itself is passed to service methods
    // that then extract the ID. But can be useful.
    private Long getCurrentUserId(User currentUserPrincipal) {
        if (currentUserPrincipal == null) {
            // This should ideally not happen if endpoint is secured and token is valid
            throw new IllegalStateException("User principal not found. Endpoint might not be secured correctly or token is invalid.");
        }
        return currentUserPrincipal.getId();
    }


    @PostMapping
    @PreAuthorize("isAuthenticated()") // Any authenticated user can create a task for themselves
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskRequestDto taskRequestDto,
            @AuthenticationPrincipal User currentUserPrincipal) {

        Long userId = getCurrentUserId(currentUserPrincipal);
        TaskResponseDto createdTask = taskService.createTask(taskRequestDto, userId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()") // User must be authenticated
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUserPrincipal) {
        // The service layer handles the logic to check if the user owns the task or is an admin
        Long requestingUserId = getCurrentUserId(currentUserPrincipal);
        TaskResponseDto task = taskService.getTaskById(taskId, requestingUserId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/user/my-tasks") // Endpoint to get tasks for the currently authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponseDto>> getCurrentUserTasks(
            @AuthenticationPrincipal User currentUserPrincipal) {

        Long userId = getCurrentUserId(currentUserPrincipal);
        List<TaskResponseDto> tasks = taskService.getAllTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    // Example: Admin endpoint to get tasks for any user
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ROLE_ADMIN can access this
    public ResponseEntity<List<TaskResponseDto>> getTasksByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal User adminPrincipal) { // Optional: use adminPrincipal for logging/auditing

        // Here, adminPrincipal.getId() would be the admin's ID.
        // We are fetching tasks for the 'userId' specified in the path.
        List<TaskResponseDto> tasks = taskService.getAllTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequestDto taskRequestDto,
            @AuthenticationPrincipal User currentUserPrincipal) {

        Long requestingUserId = getCurrentUserId(currentUserPrincipal);
        TaskResponseDto updatedTask = taskService.updateTask(taskId, taskRequestDto, requestingUserId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUserPrincipal) {

        Long requestingUserId = getCurrentUserId(currentUserPrincipal);
        taskService.deleteTask(taskId, requestingUserId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}