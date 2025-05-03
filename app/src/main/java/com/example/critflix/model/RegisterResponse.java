package com.example.critflix.model;

public class RegisterResponse {
    private User user;
    private String token;
    private String message;


    // Constructor vacío
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

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
