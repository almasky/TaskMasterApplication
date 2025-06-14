package com.maj.TaskMasterApplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private boolean completed = false;

    private LocalDateTime createdAt = LocalDateTime.now(); // Will be overridden by @PrePersist on new entities

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM; // This now refers to the top-level Priority enum

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) { // Good practice to check if it's already set
            createdAt = LocalDateTime.now();
        }
        // Ensure default priority if not set
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
        // Ensure default completed status if not set (though boolean defaults to false)
        // completed = false; // Redundant as boolean primitive defaults to false
    }
}
