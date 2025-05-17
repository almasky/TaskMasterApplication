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
    public ResponseEntity<Admin> register(@Valid @RequestBody Admin admin) {
        Admin savedAdmin = adminService.register(admin);
        return ResponseEntity.ok(savedAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Admin admin = adminService.login(request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(admin.getEmail());
            return ResponseEntity.ok().body(
                Map.of("token", token, "email", admin.getEmail(), "username", admin.getUsername())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin management endpoints
    @GetMapping("/admins")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/admins/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admins/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @Valid @RequestBody Admin admin) {
        return adminService.getAdminById(id)
            .map(existingAdmin -> {
                admin.setId(id);
                return ResponseEntity.ok(adminService.saveAdmin(admin));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        return adminService.getAdminById(id)
            .map(admin -> {
                adminService.deleteAdmin(id);
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // User management endpoints
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return adminService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = adminService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return adminService.getUserById(id)
            .map(existingUser -> {
                user.setId(id);
                return ResponseEntity.ok(adminService.saveUser(user));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return adminService.getUserById(id)
            .map(user -> {
                adminService.deleteUser(id);
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Task management endpoints
    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return adminService.getAllTasks();
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return adminService.getTaskById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task savedTask = adminService.saveTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return adminService.getTaskById(id)
            .map(existingTask -> {
                task.setId(id);
                return ResponseEntity.ok(adminService.saveTask(task));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return adminService.getTaskById(id)
            .map(task -> {
                adminService.deleteTask(id);
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tasks/completed")
    public List<Task> getCompletedTasks() {
        return adminService.getCompletedTasks();
    }

    @GetMapping("/tasks/pending")
    public List<Task> getPendingTasks() {
        return adminService.getPendingTasks();
    }

    @GetMapping("/tasks/search")
    public List<Task> searchTasks(@RequestParam String keyword) {
        return adminService.searchTasks(keyword);
    }

    @GetMapping("/tasks/overdue")
    public List<Task> getOverdueTasks() {
        return adminService.getOverdueTasks();
    }

    @GetMapping("/tasks/priority/{priority}")
    public List<Task> getTasksByPriority(@PathVariable Task.Priority priority) {
        return adminService.getTasksByPriority(priority);
    }

    @GetMapping("/tasks/user/{userId}")
    public List<Task> getTasksByUserId(@PathVariable Long userId) {
        return adminService.getTasksByUserId(userId);
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        return adminService.getTaskById(id)
            .map(existingTask -> {
                existingTask.setCompleted(!existingTask.isCompleted());
                return ResponseEntity.ok(adminService.saveTask(existingTask));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
