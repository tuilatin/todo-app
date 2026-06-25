package com.example.todo_app.service;

import com.example.todo_app.dto.TodoDTO;
import com.example.todo_app.dto.TodoRequestDTO;
import java.util.List;

public interface TodoService {
    List<TodoDTO> getAllTodos();
    List<TodoDTO> searchTodos(String keyword, Long categoryId, Boolean completed);
    TodoDTO createTodo(TodoRequestDTO dto);
    TodoDTO updateTodo(Long id, TodoRequestDTO dto); // Nên dùng DTO để đồng bộ dữ liệu sửa
    void deleteTodo(Long id);
    TodoDTO getTodoById(Long id);
}