package com.example.critflix.model;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private Usuario user;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUser() {
        return user;
    }
}
