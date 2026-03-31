package com.todo.controller;

import com.todo.dto.ProjectRequest;
import com.todo.dto.ProjectResponse;
import com.todo.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(Authentication authentication) {
        return ResponseEntity.ok(projectService.getAllProjects(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(Authentication authentication,
                                                         @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.createProject(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(Authentication authentication,
                                                         @PathVariable Long id,
                                                         @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(Authentication authentication, @PathVariable Long id) {
        projectService.deleteProject(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
