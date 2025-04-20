package com.example.critflix.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ComentarioRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("tmdb_id") val tmdbId: Int,
    val tipo: String,
    val comentario: String,
    @SerializedName("es_spoiler") val esSpoiler: Boolean = false
)