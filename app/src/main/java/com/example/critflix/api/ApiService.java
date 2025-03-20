package com.example.critflix.api;

import com.example.critflix.model.LoginRequest;
import com.example.critflix.model.LoginResponse;
import com.example.critflix.model.RegisterRequest;
import com.example.critflix.model.RegisterResponse;
import com.example.critflix.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("usuarios/{id}")
    Call<User> getUserProfile(@Path("id") int userId);


}
