package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.ApiService
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.Comentario
import com.example.critflix.model.ComentarioRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComentariosViewModel : ViewModel() {

    private val _comentarios = MutableLiveData<List<Comentario>>()
    val comentarios: LiveData<List<Comentario>> = _comentarios

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Make this available for resetting in UI
    val _comentarioCreado = MutableLiveData<Comentario?>()
    val comentarioCreado: LiveData<Comentario?> = _comentarioCreado

    fun getComentariosByTmdbId(tmdbId: Int, tipo: String, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        apiService.getComentariosByTmdbId(tmdbId, tipo).enqueue(object : Callback<List<Comentario>> {
            override fun onResponse(call: Call<List<Comentario>>, response: Response<List<Comentario>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _comentarios.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("ComentariosVM", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Comentario>>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("ComentariosVM", "Error: ${t.message}")
            }
        })
    }

    fun createComentario(userId: Int, tmdbId: Int, tipo: String, comentario: String, esSpoiler: Boolean = false, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        val request = ComentarioRequest(userId, tmdbId, tipo, comentario, esSpoiler)

        apiService.createComentario(request).enqueue(object : Callback<Comentario> {
            override fun onResponse(call: Call<Comentario>, response: Response<Comentario>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val nuevoComentario = response.body()
                    _comentarioCreado.value = nuevoComentario

                    // Update the comments list with the new comment
                    if (nuevoComentario != null) {
                        val currentList = _comentarios.value ?: emptyList()
                        _comentarios.value = listOf(nuevoComentario) + currentList
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("ComentariosVM", "Error al crear comentario: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Comentario>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("ComentariosVM", "Error al crear comentario: ${t.message}")
            }
        })
    }

    fun deleteComentario(comentarioId: Int, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)


        apiService.deleteComentario(comentarioId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    // Eliminar el comentario de la lista
                    val currentList = _comentarios.value ?: emptyList()
                    _comentarios.value = currentList.filter { it.id != comentarioId }
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("ComentariosVM", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("ComentariosVM", "Error: ${t.message}")
            }
        })
    }

    fun clearError() {
        _error.value = null
    }
}