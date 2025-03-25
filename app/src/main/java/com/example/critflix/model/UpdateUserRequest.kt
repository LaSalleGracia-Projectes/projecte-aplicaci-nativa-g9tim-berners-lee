package com.example.critflix.model

data class UpdateUserRequest(
    val name: String? = null,
    val biografia: String? = null,
    val foto_perfil: String? = null
)