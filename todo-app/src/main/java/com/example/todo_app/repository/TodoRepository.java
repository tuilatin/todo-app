package com.example.todo_app.repository;

import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findTodoByTitleContaining(String keyword);
    List<Todo> findByUser(User user);

    @Query("SELECT t FROM Todo t WHERE " +
            "(:keyword IS NULL OR t.title LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
            "(:completed IS NULL OR t.completed = :completed)")
    List<Todo> searchTodos(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("completed") Boolean completed);

    Boolean completed(boolean completed);
}