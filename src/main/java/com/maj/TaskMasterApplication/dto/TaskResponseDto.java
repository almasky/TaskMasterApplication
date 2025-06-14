package com.maj.TaskMasterApplication.dto;

import com.maj.TaskMasterApplication.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private Priority priority;
    private Long userId; // To show which user this task belongs to
    private String username; // Optionally, show the username as well
}