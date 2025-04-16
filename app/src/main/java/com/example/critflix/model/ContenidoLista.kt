package com.example.critflix.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ContenidoLista(
    val id: String = "",
    val id_lista: String = "",
    val tmdb_id: Int = 0,
    val tipo: String = "",
    val fecha_agregado: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
) {
    companion object {
        fun fromApiResponse(map: Map<String, Any>): ContenidoLista {
            val id = map["id"]?.toString() ?: ""
            val listaId = map["id_lista"]?.toString() ?: ""
            val tmdbId = (map["tmdb_id"] as? Number)?.toInt() ?: 0
            val tipo = map["tipo"]?.toString() ?: "pelicula"
            val fechaAgregado = map["fecha_agregado"]?.toString() ?:
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            return ContenidoLista(
                id = id,
                id_lista = listaId,
                tmdb_id = tmdbId,
                tipo = tipo,
                fecha_agregado = fechaAgregado
            )
        }
    }
}