package com.example.todo_app.dto;

public class AuthenticationResponse {
    private String token;

    // Constructor không tham số
    public AuthenticationResponse() {}

    // Constructor có tham số
    public AuthenticationResponse(String token) {
        this.token = token;
    }

    // Getter và Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}