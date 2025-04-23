package com.example.critflix.model

import com.google.gson.annotations.SerializedName

data class Comentario(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("tmdb_id") val tmdbId: Int,
    val tipo: String,
    val comentario: String,
    @SerializedName("es_spoiler") private val _esSpoiler: Any,
    @SerializedName("destacado") private val _destacado: Any,
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

    val destacado: Boolean
        get() = when(_destacado) {
            is Boolean -> _destacado
            is Number -> _destacado.toInt() != 0
            is String -> _destacado == "true" || _destacado == "1"
            else -> false
        }
}