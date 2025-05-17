package com.maj.TaskMasterApplication.service;

import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now()); // Ensuring timestamp is set
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());
        task.setDueDate(updatedTask.getDueDate());
        task.setPriority(updatedTask.getPriority());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task with ID " + id + " not found");
        }
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
        return taskRepository.findByDueDateBefore(LocalDateTime.now())
                .stream().sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .toList();
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }
}
