package com.example.todo_app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.todo_app.dto.TodoDTO;
import com.example.todo_app.dto.TodoRequestDTO;
import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;
import com.example.todo_app.model.Category;
import com.example.todo_app.repository.TodoRepository;
import com.example.todo_app.repository.CategoryRepository;
import com.example.todo_app.service.TodoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // 💡 Hàm phụ bảo mật: Lấy ra chính xác User đang gửi request thông qua JWT Token
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private TodoDTO convertToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setCompleted(todo.isCompleted());
        dto.setCategoryName(todo.getCategory() != null
                ? todo.getCategory().getTitle()
                : "Chưa phân loại");
        return dto;
    }

    @Override
    public List<TodoDTO> getAllTodos() {
        User currentUser = getCurrentUser();
        // 🌟 SỬA LỖI: Chỉ lấy Todo của RIÊNG người đang đăng nhập
        return todoRepository.findByUser(currentUser)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TodoDTO createTodo(TodoRequestDTO dto) {
        User currentUser = getCurrentUser();

        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());
        todo.setUser(currentUser); // 🌟 SỬA LỖI: Buộc phải gắn chủ sở hữu cho công việc này

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            todo.setCategory(category);
        }

        Todo saved = todoRepository.save(todo);
        return convertToDTO(saved);
    }

    @Override
    public TodoDTO updateTodo(Long id, TodoRequestDTO dto) {
        User currentUser = getCurrentUser();

        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc này"));

        // 🌟 BẢO MẬT: Kiểm tra xem Todo cần sửa có phải của ông đang đăng nhập không
        if (!existing.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa công việc của người khác!");
        }

        existing.setTitle(dto.getTitle());
        existing.setCompleted(dto.isCompleted());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            existing.setCategory(category);
        } else {
            existing.setCategory(null);
        }

        Todo saved = todoRepository.save(existing);
        return convertToDTO(saved);
    }

    @Override
    public void deleteTodo(Long id) {
        User currentUser = getCurrentUser();
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc này"));

        // 🌟 BẢO MẬT: Chặn không cho xóa nhầm Todo của người khác
        if (!existing.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa công việc của người khác!");
        }

        todoRepository.delete(existing);
    }

    @Override
    public TodoDTO getTodoById(Long id) {
        return null;
    }

    @Override
    public List<TodoDTO> searchTodos(String keyword, Long categoryId, Boolean completed) {
        // Hàm này bạn có thể bổ sung điều kiện lọc theo User dưới Repository sau
        return todoRepository.searchTodos(keyword, categoryId, completed)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}