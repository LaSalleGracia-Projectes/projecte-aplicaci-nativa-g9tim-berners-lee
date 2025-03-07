package com.example.critflix.model;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String password_confirmation;  // Añadido para cumplir con la validación backend
    private String foto_perfil;
    private String biografia;
    private String rol;

    // Constructor sin parámetros
    public RegisterRequest() {}

    // Constructor con campos obligatorios
    public RegisterRequest(String name, String email, String password, String password_confirmation) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }

    // Constructor con todos los campos
    public RegisterRequest(
            String name,
            String email,
            String password,
            String password_confirmation,
            String foto_perfil,
            String biografia,
            String rol
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.foto_perfil = foto_perfil;
        this.biografia = biografia;
        this.rol = rol;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public void setFoto_perfil(String foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}