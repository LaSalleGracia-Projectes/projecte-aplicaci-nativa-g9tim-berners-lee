package com.example.critflix.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String foto_perfil;
    private String biografia;
    private String rol;

    // Constructor
    public User() {}

    // Getters y setters para todos los campos
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getFotoPerfil() {
        return foto_perfil;
    }

    public void setFotoPerfil(String foto_perfil) {
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