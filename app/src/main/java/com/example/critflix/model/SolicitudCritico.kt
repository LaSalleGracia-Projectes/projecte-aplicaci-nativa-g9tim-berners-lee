package com.example.critflix.model

data class SolicitudCritico(
    val id: Int,
    val user_id: Int,
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val motivo: String,
    val estado: String,
    val created_at: String,
    val updated_at: String,
    val usuario: User? = null
)

data class SolicitudCriticoRequest(
    val user_id: Int,
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val motivo: String
)

data class SolicitudCriticoResponse(
    val message: String,
    val solicitud: SolicitudCritico? = null
)