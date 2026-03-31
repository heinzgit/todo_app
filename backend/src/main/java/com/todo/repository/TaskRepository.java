package com.todo.repository;

import com.todo.entity.Task;
import com.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByPriorityAsc(User user);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate >= :date ORDER BY t.priority ASC")
    List<Task> findByUserAndDueDateGreaterThanEqual(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate = :date ORDER BY t.priority ASC")
    List<Task> findByUserAndDueDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate < :today AND t.completed = false ORDER BY t.dueDate DESC")
    List<Task> findIncompleteTasksBeforeToday(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate >= :today ORDER BY t.priority ASC")
    List<Task> findTodayAndFutureTasks(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.completed = true ORDER BY t.completedAt DESC")
    List<Task> findCompletedTasks(@Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate = :today ORDER BY t.completed ASC, t.priority ASC")
    List<Task> findTodayTasks(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.completed = false ORDER BY t.priority ASC")
    List<Task> findIncompleteTasks(@Param("user") User user);
}
