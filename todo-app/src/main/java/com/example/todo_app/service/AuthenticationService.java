package com.example.todo_app.service;

import com.example.todo_app.model.User;
import com.example.todo_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.todo_app.dto.AuthenticationRequest;
import com.example.todo_app.dto.AuthenticationResponse;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Nhờ AuthenticationManager kiểm tra tài khoản và mật khẩu
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Nếu không có lỗi gì ở trên, tiến hành tìm User dưới DB và bóc bọc
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng này"));

        // 3. Dùng JwtService (máy in thẻ) để đúc ra 1 chuỗi Token cho user này
        String jwtToken = jwtService.generateToken((User) user);

        // 4. Bỏ chuỗi Token vào chiếc hộp Response và trả về
        return new AuthenticationResponse(jwtToken);
    }
}