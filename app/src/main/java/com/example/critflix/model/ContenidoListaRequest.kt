package com.example.critflix.model

data class ContenidoListaRequest(
    val id_lista: String,
    val tmdb_id: Int,
    val tipo: String
)