package com.todo.repository;

import com.todo.entity.Project;
import com.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserOrderByCreatedAtDesc(User user);
}
