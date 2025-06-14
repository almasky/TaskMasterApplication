package com.maj.TaskMasterApplication.service;

import com.maj.TaskMasterApplication.dto.TaskRequestDto;
import com.maj.TaskMasterApplication.dto.TaskResponseDto;

import java.util.List;

public interface TaskService {

    /**
     * Creates a new task for a specified user.
     * @param taskRequestDto DTO containing task details.
     * @param userId The ID of the user for whom the task is being created.
     * @return The created task as a DTO.
     */
    TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long userId);

    /**
     * Retrieves a specific task by its ID.
     * Ensures the task belongs to the requesting user or the user is an admin.
     * @param taskId The ID of the task to retrieve.
     * @param userId The ID of the user making the request.
     * @return The task DTO if found and authorized.
     */
    TaskResponseDto getTaskById(Long taskId, Long userId); // Consider adding requesting user details for auth checks

    /**
     * Retrieves all tasks for a specific user.
     * @param userId The ID of the user whose tasks are to be retrieved.
     * @return A list of task DTOs.
     */
    List<TaskResponseDto> getAllTasksByUserId(Long userId);

    /**
     * Updates an existing task.
     * Ensures the task belongs to the requesting user or the user is an admin.
     * @param taskId The ID of the task to update.
     * @param taskRequestDto DTO containing updated task details.
     * @param userId The ID of the user making the request.
     * @return The updated task DTO.
     */
    TaskResponseDto updateTask(Long taskId, TaskRequestDto taskRequestDto, Long userId); // Consider requesting user

    /**
     * Deletes a task by its ID.
     * Ensures the task belongs to the requesting user or the user is an admin.
     * @param taskId The ID of the task to delete.
     * @param userId The ID of the user making the request.
     */
    void deleteTask(Long taskId, Long userId); // Consider requesting user

    // Optional: Methods for admins to manage any task, or more complex queries
    // List<TaskResponseDto> getAllTasks(); // For an admin
}