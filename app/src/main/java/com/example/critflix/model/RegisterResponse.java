package com.example.critflix.model;

public class RegisterResponse {
    private User user;
    private String token;

    // Constructor vacío para Gson
    public RegisterResponse() {}

    // Getters y setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
