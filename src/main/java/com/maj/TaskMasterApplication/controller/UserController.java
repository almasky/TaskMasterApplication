package com.maj.TaskMasterApplication.controller;

import com.maj.TaskMasterApplication.model.Admin;
import com.maj.TaskMasterApplication.model.LoginRequest;
import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.repository.TaskRepository;
import com.maj.TaskMasterApplication.repository.UserRepository;
import com.maj.TaskMasterApplication.security.JwtUtil;
import com.maj.TaskMasterApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        User savedUser = userService.register(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok().body(
                    java.util.Map.of("token", token, "email", user.getEmail())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    private User getCurrentUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get all tasks of the logged-in user
    @GetMapping
    public List<Task> getUserTasks(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        return taskRepository.findByUser(user);
    }

    // Create a new task for the logged-in user
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Task task) {
        User user = getCurrentUser(authHeader);
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    // Update a task only if it belongs to logged-in user
    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long taskId,
                                        @RequestBody Task updatedTask) {
        User user = getCurrentUser(authHeader);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't modify this task");
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDueDate(updatedTask.getDueDate());
        task.setPriority(updatedTask.getPriority());
        task.setCompleted(updatedTask.isCompleted());

        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    // Delete a task only if it belongs to logged-in user
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long taskId) {
        User user = getCurrentUser(authHeader);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't delete this task");
        }

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
}
