package com.example.critflix.model

data class Valoracion(
    val id: Int? = null,
    val user_id: Int,
    val tmdb_id: Int,
    val valoracion: String = "like"
)

data class ValoracionRequest(
    val user_id: Int,
    val tmdb_id: Int,
    val valoracion: String = "like"
)

data class ValoracionResponse(
    val id: Int,
    val user_id: Int,
    val tmdb_id: Int,
    val valoracion: String,
    val id_tmdb: Int = tmdb_id
)
