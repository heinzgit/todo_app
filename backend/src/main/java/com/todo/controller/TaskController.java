package com.todo.controller;

import com.todo.dto.ReorderRequest;
import com.todo.dto.RescheduleRequest;
import com.todo.dto.TaskRequest;
import com.todo.dto.TaskResponse;
import com.todo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getHomeTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getHomeTasks(authentication.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponse>> getAllTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getAllTasks(authentication.getName()));
    }

    @GetMapping("/past-completed")
    public ResponseEntity<List<TaskResponse>> getPastCompletedTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getPastCompletedTasks(authentication.getName()));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TaskResponse>> getTodayTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getTodayTasks(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(Authentication authentication,
                                                     @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(Authentication authentication,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable Long id) {
        taskService.deleteTask(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> toggleComplete(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.toggleComplete(authentication.getName(), id));
    }

    @PostMapping("/{id}/copy-to-today")
    public ResponseEntity<TaskResponse> copyToToday(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.copyToToday(authentication.getName(), id));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<TaskResponse> reschedule(Authentication authentication,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(taskService.reschedule(authentication.getName(), id, request.getNewDueDate()));
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<TaskResponse>> reorder(Authentication authentication,
                                                       @RequestBody ReorderRequest request) {
        return ResponseEntity.ok(taskService.reorder(authentication.getName(), request.getTaskIds()));
    }
}
