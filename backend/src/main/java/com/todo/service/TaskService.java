package com.todo.service;

import com.todo.dto.TaskRequest;
import com.todo.dto.TaskResponse;
import com.todo.entity.Task;
import com.todo.entity.User;
import com.todo.exception.ResourceNotFoundException;
import com.todo.repository.TaskRepository;
import com.todo.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getHomeTasks(String username) {
        User user = getUserByUsername(username);
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<TaskResponse> result = new ArrayList<>();

        List<Task> yesterdayIncomplete = taskRepository.findIncompleteTasksBeforeToday(user, today);
        for (Task task : yesterdayIncomplete) {
            if (task.getDueDate().isEqual(yesterday) || task.getDueDate().isBefore(today)) {
                result.add(toResponse(task));
            }
        }

        List<Task> todayAndFuture = taskRepository.findTodayAndFutureTasks(user, today);
        result.addAll(todayAndFuture.stream().map(this::toResponse).collect(Collectors.toList()));

        result.sort((a, b) -> a.getPriority().compareTo(b.getPriority()));

        return result;
    }

    public List<TaskResponse> getPastCompletedTasks(String username) {
        User user = getUserByUsername(username);
        return taskRepository.findCompletedTasks(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTodayTasks(String username) {
        User user = getUserByUsername(username);
        return taskRepository.findIncompleteTasks(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getAllTasks(String username) {
        User user = getUserByUsername(username);
        return taskRepository.findByUserOrderByPriorityAsc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(String username, TaskRequest request) {
        User user = getUserByUsername(username);

        Integer maxPriority = taskRepository.findByUserOrderByPriorityAsc(user)
                .stream()
                .map(Task::getPriority)
                .max(Integer::compareTo)
                .orElse(0);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCompleted(false);
        task.setPriority(maxPriority + 1);
        task.setRescheduleCount(0);
        task.setTaskPriority(request.getTaskPriority() != null ? request.getTaskPriority() : "medium");
        task.setUser(user);

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(String username, Long taskId, TaskRequest request) {
        User user = getUserByUsername(username);
        Task task = getTaskByIdAndUser(taskId, user);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (!task.getDueDate().equals(request.getDueDate())) {
            task.setDueDate(request.getDueDate());
            task.setRescheduleCount(task.getRescheduleCount() + 1);
        }

        if (request.getTaskPriority() != null) {
            task.setTaskPriority(request.getTaskPriority());
        }

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(String username, Long taskId) {
        User user = getUserByUsername(username);
        Task task = getTaskByIdAndUser(taskId, user);
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse toggleComplete(String username, Long taskId) {
        User user = getUserByUsername(username);
        Task task = getTaskByIdAndUser(taskId, user);
        boolean newCompleted = !task.getCompleted();
        task.setCompleted(newCompleted);
        if (newCompleted) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse copyToToday(String username, Long taskId) {
        User user = getUserByUsername(username);
        Task original = getTaskByIdAndUser(taskId, user);

        Task copy = new Task();
        copy.setTitle(original.getTitle());
        copy.setDescription(original.getDescription());
        copy.setDueDate(LocalDate.now());
        copy.setCompleted(false);
        copy.setPriority(getNextPriority(user));
        copy.setRescheduleCount(0);
        copy.setTaskPriority(original.getTaskPriority());
        copy.setUser(user);

        return toResponse(taskRepository.save(copy));
    }

    @Transactional
    public TaskResponse reschedule(String username, Long taskId, LocalDate newDueDate) {
        User user = getUserByUsername(username);
        Task task = getTaskByIdAndUser(taskId, user);
        task.setDueDate(newDueDate);
        task.setRescheduleCount(task.getRescheduleCount() + 1);
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public List<TaskResponse> reorder(String username, List<Long> taskIds) {
        User user = getUserByUsername(username);

        for (int i = 0; i < taskIds.size(); i++) {
            Task task = getTaskByIdAndUser(taskIds.get(i), user);
            task.setPriority(i);
            taskRepository.save(task);
        }

        return getAllTasks(username);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Task getTaskByIdAndUser(Long taskId, User user) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private Integer getNextPriority(User user) {
        return taskRepository.findByUserOrderByPriorityAsc(user)
                .stream()
                .map(Task::getPriority)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getCompleted(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCompletedAt(),
                task.getRescheduleCount(),
                task.getTaskPriority()
        );
    }
}
