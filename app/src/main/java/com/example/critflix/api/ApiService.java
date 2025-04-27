package com.example.critflix.api;

import com.example.critflix.model.Comentario;
import com.example.critflix.model.ComentarioRequest;
import com.example.critflix.model.ContenidoLista;
import com.example.critflix.model.ContenidoListaRequest;
import com.example.critflix.model.LikeComentarioRequest;
import com.example.critflix.model.LikeResponse;
import com.example.critflix.model.LikeStatusResponse;
import com.example.critflix.model.LikesCountResponse;
import com.example.critflix.model.Lista;
import com.example.critflix.model.ListaRequest;
import com.example.critflix.model.LoginRequest;
import com.example.critflix.model.LoginResponse;
import com.example.critflix.model.Notification;
import com.example.critflix.model.RegisterRequest;
import com.example.critflix.model.RegisterResponse;
import com.example.critflix.model.UpdateUserRequest;
import com.example.critflix.model.User;
import com.example.critflix.model.ValoracionRequest;
import com.example.critflix.model.ValoracionResponse;

import java.util.List;
import java.util.Map;
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
    Call<Map<String, Object>> getUserLists(@Path("userId") int userId);

    @PUT("listas/{id}")
    Call<Lista> updateList(@Path("id") String id, @Body ListaRequest listaRequest);

    @DELETE("listas/{id}")
    Call<Void> deleteList(@Path("id") String id);

    // Endpoints Contenido Listas
    @POST("contenido_listas")
    Call<ContenidoLista> addContentToList(@Body ContenidoListaRequest request);

    @GET("contenido_listas/lista/{listaId}")
    Call<Map<String, Object>> getListContent(@Path("listaId") String listaId);

    @DELETE("contenido_listas/{id}")
    Call<Void> removeContentFromList(@Path("id") String id);

    // Endpoints para Comentarios
    @GET("comentarios")
    Call<List<Comentario>> getAllComentarios();

    @POST("comentarios")
    Call<Comentario> createComentario(@Body ComentarioRequest request);

    @GET("comentarios/{id}")
    Call<Comentario> getComentario(@Path("id") int id);

    @PUT("comentarios/{id}")
    Call<Comentario> updateComentario(@Path("id") int id, @Body ComentarioRequest request);

    @DELETE("comentarios/{id}")
    Call<Void> deleteComentario(@Path("id") int id);

    @GET("comentarios/tmdb/{tmdbId}/{tipo}")
    Call<List<Comentario>> getComentariosByTmdbId(@Path("tmdbId") int tmdbId, @Path("tipo") String tipo);

    // Endpoints para Likes/Dislikes de comentarios
    @POST("likes_comentarios")
    Call<LikeResponse> likeComentario(@Body LikeComentarioRequest request);

    @GET("likes_comentarios/status/{comentarioId}/{userId}")
    Call<LikeStatusResponse> getLikeStatus(@Path("comentarioId") int comentarioId, @Path("userId") int userId);

    @GET("likes_comentarios/count/{comentarioId}")
    Call<LikesCountResponse> getLikesCount(@Path("comentarioId") int comentarioId);

    // Endpoints para Notificaciones
    @GET("notificaciones")
    Call<List<Notification>> getAllNotificaciones();

    @GET("notificaciones/user/{userId}")
    Call<List<Notification>> getUserNotificaciones(@Path("userId") int userId);

    @PUT("notificaciones/read/{id}")
    Call<Notification> markNotificationAsRead(@Path("id") int id);

    @PUT("notificaciones/read_all/{userId}")
    Call<Map<String, String>> markAllNotificationsAsRead(@Path("userId") int userId);

    // Endpoints para Valoraciones (Favoritos)
    @GET("valoraciones/usuario/{userId}")
    Call<List<ValoracionResponse>> getUserFavorites(@Path("userId") int userId);

    @POST("valoraciones")
    Call<ValoracionResponse> addFavorite(@Body ValoracionRequest request);

    @DELETE("valoraciones/{id}")
    Call<Void> removeFavorite(@Path("id") int id);

    @GET("valoraciones/check/{userId}/{peliculaId}")
    Call<Boolean> checkFavoriteStatus(@Path("userId") int userId, @Path("peliculaId") int peliculaId);
}