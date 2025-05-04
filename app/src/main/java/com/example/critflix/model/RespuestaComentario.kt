package com.example.critflix.model

import com.google.gson.annotations.SerializedName

data class RespuestaComentario(
    val id: Int,
    @SerializedName("comentario_id") val comentarioId: Int,
    @SerializedName("user_id") val userId: Int,
    val respuesta: String,
    @SerializedName("es_spoiler") private val _esSpoiler: Any,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val usuario: User? = null
) {
    val esSpoiler: Boolean
        get() = when(_esSpoiler) {
            is Boolean -> _esSpoiler
            is Number -> _esSpoiler.toInt() != 0
            is String -> _esSpoiler == "true" || _esSpoiler == "1"
            else -> false
        }
}

data class RespuestaComentarioRequest(
    @SerializedName("comentario_id") val comentarioId: Int,
    @SerializedName("user_id") val userId: Int,
    val respuesta: String,
    @SerializedName("es_spoiler") val esSpoiler: Boolean = false
)