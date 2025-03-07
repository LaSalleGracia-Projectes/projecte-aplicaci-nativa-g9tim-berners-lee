package com.example.critflix.api;

import com.example.critflix.model.LoginRequest;
import com.example.critflix.model.LoginResponse;
import com.example.critflix.model.RegisterRequest;
import com.example.critflix.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
