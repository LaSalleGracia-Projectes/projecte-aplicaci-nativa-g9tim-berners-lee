package com.example.critflix.model

data class Valoracion(
    val id: Int? = null,
    val user_id: Int,
    val id_pelicula: Int,
    val valoracion: String = "like"
)

data class ValoracionRequest(
    val user_id: Int,
    val id_pelicula: Int,
    val valoracion: String = "like"
)

data class ValoracionResponse(
    val id: Int,
    val user_id: Int,
    val id_pelicula: Int,
    val valoracion: String,
    val pelicula_id: Int = id_pelicula
)
