package com.example.critflix.api;

import com.example.critflix.model.Lista;
import com.example.critflix.model.ListaRequest;
import com.example.critflix.model.LoginRequest;
import com.example.critflix.model.LoginResponse;
import com.example.critflix.model.RegisterRequest;
import com.example.critflix.model.RegisterResponse;
import com.example.critflix.model.UpdateUserRequest;
import com.example.critflix.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Endpoints Registro / Inicio de sesión
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);


    // Endpoints Usuarios
    @GET("usuarios/{id}")
    Call<User> getUserProfile(@Path("id") int userId);

    @PUT("usuarios/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body UpdateUserRequest updateUserRequest);


    // Endpoints Listas
    @POST("listas")
    Call<Lista> createList(@Body ListaRequest listaRequest);

    @GET("listas/user/{userId}")
    Call<List<Lista>> getUserLists(@Path("userId") int userId);

    @PUT("listas/{id}")
    Call<Lista> updateList(@Path("id") String id, @Body ListaRequest listaRequest);

    @DELETE("listas/{id}")
    Call<Void> deleteList(@Path("id") String id);
}
