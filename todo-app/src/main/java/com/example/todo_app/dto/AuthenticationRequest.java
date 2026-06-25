package com.example.todo_app.dto;

public class AuthenticationRequest {
    private String username;
    private String password;

    // Construtor không tham số (bắt buộc phải có để Spring ép kiểu từ JSON)
    public AuthenticationRequest() {}

    // Constructor có tham số
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Các hàm Getter và Setter để lấy và ghi dữ liệu
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}