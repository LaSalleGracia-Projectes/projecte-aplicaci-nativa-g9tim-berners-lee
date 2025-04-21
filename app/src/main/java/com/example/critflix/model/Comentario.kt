package com.example.critflix.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Comentario(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("tmdb_id") val tmdbId: Int,
    val tipo: String,
    val comentario: String,
    @SerializedName("es_spoiler") val esSpoilerValue: Int,
    @SerializedName("destacado") val destacadoValue: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val usuario: User? = null
) {
    val esSpoiler: Boolean
        get() = esSpoilerValue != 0

    val destacado: Boolean
        get() = destacadoValue != 0
}