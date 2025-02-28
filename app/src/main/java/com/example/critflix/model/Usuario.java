package com.example.critflix.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    private int id;
    private String name;
    private String email;
    private transient String password;
    @SerializedName("foto_perfil")
    private String fotoPerfil;
    private String biografia;
    private String rol;

    public Usuario(int id, String name, String email, String password, String foto_perfil, String biografia, String rol) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.fotoPerfil = foto_perfil;
        this.biografia = biografia;
        this.rol = rol;
    }

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

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
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
