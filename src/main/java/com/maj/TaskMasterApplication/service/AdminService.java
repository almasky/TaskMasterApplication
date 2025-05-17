package com.maj.TaskMasterApplication.service;

import com.maj.TaskMasterApplication.model.Admin;
import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.repository.AdminRepository;
import com.maj.TaskMasterApplication.repository.TaskRepository;
import com.maj.TaskMasterApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(AdminRepository adminRepository, UserRepository userRepository,
                        TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Admin management methods
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Optional<Admin> getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public Admin register(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Admin login(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isEmpty() || !passwordEncoder.matches(password, adminOpt.get().getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!adminOpt.get().isActive()) {
            throw new IllegalArgumentException("Admin account is inactive");
        }

        return adminOpt.get();
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    public List<Admin> getActiveAdmins() {
        return adminRepository.findByActive(true);
    }

    // User management methods
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Task management methods
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompleted(true);
    }

    public List<Task> getPendingTasks() {
        return taskRepository.findByCompleted(false);
    }

    public List<Task> searchTasks(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Task> getOverdueTasks() {
        return taskRepository.findByDueDateBefore(LocalDateTime.now());
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }
}
