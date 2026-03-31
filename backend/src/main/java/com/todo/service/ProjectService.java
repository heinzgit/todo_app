package com.todo.service;

import com.todo.dto.ProjectRequest;
import com.todo.dto.ProjectResponse;
import com.todo.entity.Project;
import com.todo.entity.User;
import com.todo.exception.ResourceNotFoundException;
import com.todo.repository.ProjectRepository;
import com.todo.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectResponse> getAllProjects(String username) {
        User user = getUserByUsername(username);
        return projectRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse createProject(String username, ProjectRequest request) {
        User user = getUserByUsername(username);

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setUser(user);

        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProject(String username, Long projectId, ProjectRequest request) {
        User user = getUserByUsername(username);
        Project project = getProjectByIdAndUser(projectId, user);

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(String username, Long projectId) {
        User user = getUserByUsername(username);
        Project project = getProjectByIdAndUser(projectId, user);
        projectRepository.delete(project);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Project getProjectByIdAndUser(Long projectId, User user) {
        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }
}
