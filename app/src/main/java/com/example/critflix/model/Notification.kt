package com.example.critflix.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Notification(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val mensaje: String,
    val tipo: String,
    val leido: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)