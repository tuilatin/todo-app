package com.example.todo_app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.todo_app.model.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateToken(User username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", username.getRole());
        return createToken(claims, username.getUsername());
    }

    private Key getSignInKey(){
        // 1. Giải mã chuỗi secret key từ base64
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 2. Tạo đối tượng Key từ mảng byte đã giải mã
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    // Hàm lấy Tên đăng nhập (Username)
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject(); // Vì lúc tạo ta dùng setSubject(), nên JJWT cung cấp sẵn hàm getSubject()
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 2. Kiểm tra Token có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
