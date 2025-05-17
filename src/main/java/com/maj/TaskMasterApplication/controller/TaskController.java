package com.maj.TaskMasterApplication.controller;

import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task savedTask = taskService.saveTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    task.setId(id);
                    return ResponseEntity.ok(taskService.saveTask(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> {
                    taskService.deleteTask(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/completed")
    public List<Task> getCompletedTasks() {
        return taskService.getCompletedTasks();
    }

    @GetMapping("/pending")
    public List<Task> getPendingTasks() {
        return taskService.getPendingTasks();
    }

    @GetMapping("/search")
    public List<Task> searchTasks(@RequestParam String keyword) {
        return taskService.searchTasks(keyword);
    }

    @GetMapping("/overdue")
    public List<Task> getOverdueTasks() {
        return taskService.getOverdueTasks();
    }

    @GetMapping("/priority/{priority}")
    public List<Task> getTasksByPriority(@PathVariable Task.Priority priority) {
        return taskService.getTasksByPriority(priority);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    existingTask.setCompleted(!existingTask.isCompleted());
                    return ResponseEntity.ok(taskService.saveTask(existingTask));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
