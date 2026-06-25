package com.example.todo_app;

import com.example.todo_app.model.Category;
import com.example.todo_app.model.Role;
import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;
import com.example.todo_app.repository.CategoryRepository;
import com.example.todo_app.repository.TodoRepository;
import com.example.todo_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User defaultUser = new User();
            defaultUser.setUsername("user1");
            defaultUser.setPassword(passwordEncoder.encode("123456"));
            defaultUser.setRole(Role.USER);
            userRepository.save(defaultUser);
        }

        if (categoryRepository.count() == 0) {
            Category congViec = new Category();
            congViec.setTitle("Công việc");
            categoryRepository.save(congViec);

            Category hocTap = new Category();
            hocTap.setTitle("Học tập");
            categoryRepository.save(hocTap);

            Category caNhan = new Category();
            caNhan.setTitle("Cá nhân");
            categoryRepository.save(caNhan);
        }

        if (todoRepository.count() == 0) {
            Category congViec = categoryRepository.findById(1L).orElse(null);

            Todo todo1 = new Todo();
            todo1.setTitle("Học Spring Boot");
            todo1.setCompleted(false);
            todo1.setCategory(congViec);
            todoRepository.save(todo1);

            Todo todo2 = new Todo();
            todo2.setTitle("Làm Todo App");
            todo2.setCompleted(true);
            todo2.setCategory(congViec);
            todoRepository.save(todo2);

            Todo todo3 = new Todo();
            todo3.setTitle("Học tiếng Anh");
            todo3.setCompleted(true);
            todo3.setCategory(congViec);
            todoRepository.save(todo3);
        }
    }
}