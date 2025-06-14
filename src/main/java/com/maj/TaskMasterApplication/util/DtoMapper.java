package com.maj.TaskMasterApplication.util;

import com.maj.TaskMasterApplication.dto.TaskRequestDto;
import com.maj.TaskMasterApplication.dto.TaskResponseDto;
import com.maj.TaskMasterApplication.dto.UserResponseDto;
import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.model.User;

public class DtoMapper {

    public static TaskResponseDto toTaskResponseDto(Task task) {
        if (task == null) return null;
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getDueDate(),
                task.getPriority(),
                task.getUser() != null ? task.getUser().getId() : null,
                task.getUser() != null ? task.getUser().getUsername() : null
        );
    }

    public static Task toTaskEntity(TaskRequestDto dto, User user) {
        if (dto == null) return null;
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        if (dto.getCompleted() != null) {
            task.setCompleted(dto.getCompleted());
        }
        // If dueDate is not provided in DTO, it will remain null in the entity
        task.setDueDate(dto.getDueDate());
        // If priority is not provided, it will use the default from the Task entity
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        task.setUser(user); // Associate with the user
        // createdAt will be set by @PrePersist
        return task;
    }

    public static UserResponseDto toUserResponseDto(User user) {
        if (user == null) return null;
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
    }
}