package com.maj.TaskMasterApplication.dto;

import com.maj.TaskMasterApplication.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Boolean completed; // Optional on create/update, will default in entity if not provided

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDateTime dueDate; // Optional

    private Priority priority; // Optional, will default in entity

    // We'll need a way to associate this task with a user.
    // For task creation, the user ID might come from the authenticated principal
    // or explicitly if an admin is creating a task for another user.
    // For simplicity in this DTO, we might not include userId here initially,
    // as it's often derived from the security context.
    // If you need to explicitly set it via API (e.g., an admin assigning a task),
    // you could add:
    // @NotNull(message = "User ID is required when creating/assigning task explicitly")
    // private Long userId;
}
