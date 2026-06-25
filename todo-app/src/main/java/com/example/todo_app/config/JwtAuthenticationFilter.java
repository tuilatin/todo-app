package com.example.todo_app.config;

import com.example.todo_app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Lấy chuỗi "Authorization" từ Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Nếu Header trống hoặc không bắt đầu bằng "Bearer ", cho đi tiếp (để các bộ lọc khác xử lý)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Cắt bỏ "Bearer " để lấy Token
        username = jwtService.extractUsername(jwt); // Dùng JwtService để mở gói hàng

        // 3. Nếu lấy được username và người này chưa được xác thực trong phiên này
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Kiểm tra xem Token có khớp với User trong DB và chưa hết hạn không
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                // 5. "Đóng dấu" xác thực thành công vào hệ thống
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Cho request đi tiếp đến Controller
        filterChain.doFilter(request, response);
    }
}