package com.maj.TaskMasterApplication.controller;

import com.maj.TaskMasterApplication.model.Admin;
import com.maj.TaskMasterApplication.model.LoginRequest;
import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.security.JwtUtil;
import com.maj.TaskMasterApplication.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    // Admin authentication endpoints

    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@Valid @RequestBody Admin admin) {
        Admin savedAdmin = adminService.register(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        try {
            Admin admin = adminService.login(request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(admin.getEmail());
            return ResponseEntity.ok().body(
                Map.of("token", token, "email", admin.getEmail(), "role", "ADMIN")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // User management endpoints

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        validateAdmin(authHeader);
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long id) {
        validateAdmin(authHeader);
        return adminService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String authHeader,
                                           @Valid @RequestBody User user) {
        validateAdmin(authHeader);
        User createdUser = adminService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id,
                                        @Valid @RequestBody User user) {
        validateAdmin(authHeader);
        try {
            User updatedUser = adminService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id) {
        validateAdmin(authHeader);
        try {
            adminService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Task management endpoints

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks(@RequestHeader("Authorization") String authHeader) {
        validateAdmin(authHeader);
        List<Task> tasks = adminService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<Task>> getUserTasks(@RequestHeader("Authorization") String authHeader,
                                                   @PathVariable Long userId) {
        validateAdmin(authHeader);
        try {
            List<Task> tasks = adminService.getTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTaskById(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long taskId) {
        validateAdmin(authHeader);
        return adminService.getTaskById(taskId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<?> createTaskForUser(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long userId,
                                               @Valid @RequestBody Task task) {
        validateAdmin(authHeader);
        try {
            Task createdTask = adminService.createTaskForUser(userId, task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long taskId,
                                        @Valid @RequestBody Task task) {
        validateAdmin(authHeader);
        try {
            Task updatedTask = adminService.updateTask(taskId, task);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long taskId) {
        validateAdmin(authHeader);
        try {
            adminService.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method to validate admin from JWT token
    private void validateAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing authorization token");
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        adminService.getAdminByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Unauthorized: Admin access required"));
    }
}
