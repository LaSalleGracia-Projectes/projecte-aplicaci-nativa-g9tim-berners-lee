package com.example.critflix.model

data class Lista(
    val id: String,
    val name: String,
    val itemCount: Int,
    val lastUpdated: String,
    val isDefault: Boolean = false
)