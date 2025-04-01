package com.example.critflix.model

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Lista(
    val id: String,
    val name: String,
    val itemCount: Int = 0,
    val lastUpdated: String,
    val isDefault: Boolean = false,
    val user_id: Int? = null
) {
    companion object {
        fun fromApiResponse(map: Map<String, Any>): Lista {
            Log.d("Lista", "Response map: $map")

            val id = map["id"]?.toString() ?: ""
            val name = map["nombre_lista"]?.toString() ?: ""
            val userId = (map["user_id"] as? Number)?.toInt()

            return Lista(
                id = id,
                name = name,
                itemCount = 0,
                lastUpdated = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
                isDefault = false,
                user_id = userId
            )
        }
    }
}