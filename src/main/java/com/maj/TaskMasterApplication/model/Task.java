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

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime dueDate;


    //Stores enum values as readable strings instead of numerical indexes
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //This ensures createdAt is set when the entity is persisted in the database.
    //Ensures createdAt is automatically assigned when the entity is first saved in the database.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
