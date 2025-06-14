package com.maj.TaskMasterApplication.impl;

import com.maj.TaskMasterApplication.dto.TaskRequestDto;
import com.maj.TaskMasterApplication.dto.TaskResponseDto;
import com.maj.TaskMasterApplication.exception.ResourceNotFoundException;
import com.maj.TaskMasterApplication.exception.UnauthorizedAccessException;
import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.model.Roles; // For admin check
import com.maj.TaskMasterApplication.repository.TaskRepository;
import com.maj.TaskMasterApplication.repository.UserRepository;
import com.maj.TaskMasterApplication.service.TaskService;
import com.maj.TaskMasterApplication.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Spring Security's UserDetails
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice to make service methods transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Task task = DtoMapper.toTaskEntity(taskRequestDto, user);
        Task savedTask = taskRepository.save(task);
        return DtoMapper.toTaskResponseDto(savedTask);
    }

    @Override
    public TaskResponseDto getTaskById(Long taskId, Long requestingUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Requesting user not found with id: " + requestingUserId));

        // Authorization check: Task must belong to the user OR user must be an ADMIN
        if (!task.getUser().getId().equals(requestingUserId) && !requestingUser.getRole().equals(Roles.ADMIN)) {
            throw new UnauthorizedAccessException("User not authorized to access this task");
        }
        return DtoMapper.toTaskResponseDto(task);
    }

    @Override
    public List<TaskResponseDto> getAllTasksByUserId(Long userId) {
        // Ensure user exists before fetching tasks (optional, as findByUserId would return empty if user doesn't exist)
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(DtoMapper::toTaskResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto updateTask(Long taskId, TaskRequestDto taskRequestDto, Long requestingUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Requesting user not found with id: " + requestingUserId));

        if (!task.getUser().getId().equals(requestingUserId) && !requestingUser.getRole().equals(Roles.ADMIN)) {
            throw new UnauthorizedAccessException("User not authorized to update this task");
        }

        // Update fields from DTO
        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        if (taskRequestDto.getCompleted() != null) {
            task.setCompleted(taskRequestDto.getCompleted());
        }
        if (taskRequestDto.getDueDate() != null) {
            task.setDueDate(taskRequestDto.getDueDate());
        }
        if (taskRequestDto.getPriority() != null) {
            task.setPriority(taskRequestDto.getPriority());
        }
        // Note: The user of the task (owner) is generally not changed in an update operation.
        // If you need to reassign tasks, that would be a different method or require specific logic.

        Task updatedTask = taskRepository.save(task);
        return DtoMapper.toTaskResponseDto(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId, Long requestingUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Requesting user not found with id: " + requestingUserId));

        if (!task.getUser().getId().equals(requestingUserId) && !requestingUser.getRole().equals(Roles.ADMIN)) {
            throw new UnauthorizedAccessException("User not authorized to delete this task");
        }
        taskRepository.delete(task);
    }

    // Helper method to get current authenticated user's ID (will be more robust with Spring Security)
    // For now, this is a placeholder. In controller, we'd get this from Principal.
    // We pass userId explicitly to service methods for now.
    /*
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation (or Spring's User) stores username
            // And you have a way to get User entity by username
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found from security context"));
            return user.getId();
        }
        // Or if your principal is a custom object holding the ID
        // if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
        // return ((CustomUserDetails) authentication.getPrincipal()).getId();
        // }
        throw new IllegalStateException("User not authenticated or user details not available.");
    }
    */
}