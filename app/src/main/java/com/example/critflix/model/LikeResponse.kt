package com.example.critflix.model

data class LikeResponse(
    val message: String,
    val comentario: Comentario
)

data class LikeStatusResponse(
    val status: String
)

data class LikesCountResponse(
    val likes: Int,
    val dislikes: Int
)