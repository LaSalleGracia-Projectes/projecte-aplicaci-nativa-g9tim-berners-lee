package com.example.critflix.model

import com.google.gson.annotations.SerializedName

data class LikeComentarioRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("id_comentario") val idComentario: Int,
    val tipo: String
)