package com.maj.TaskMasterApplication.repository;

import com.maj.TaskMasterApplication.model.Task;
import com.maj.TaskMasterApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted(boolean completed);

    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findByDueDateBefore(LocalDateTime dateTime);

    List<Task> findByPriority(Task.Priority priority);

    List<Task> findByUser(User user);

    List<Task> findByUserId(Long userId);
}
