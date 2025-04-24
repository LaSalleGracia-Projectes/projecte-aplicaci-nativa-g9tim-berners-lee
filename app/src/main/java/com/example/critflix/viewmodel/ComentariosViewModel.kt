package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.ApiService
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.Comentario
import com.example.critflix.model.ComentarioRequest
import com.example.critflix.model.LikeComentarioRequest
import com.example.critflix.model.LikeStatusResponse
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

    val _comentarioCreado = MutableLiveData<Comentario?>()
    val comentarioCreado: LiveData<Comentario?> = _comentarioCreado

    private var lastLoadedTmdbId: Int = -1
    private var lastLoadedTipo: String = ""

    fun clearComentarios() {
        _comentarios.value = emptyList()
        _error.value = null
        _comentarioCreado.value = null
    }

    fun getComentariosByTmdbId(tmdbId: Int, tipo: String, token: String) {
        if (tmdbId == lastLoadedTmdbId && tipo == lastLoadedTipo && _comentarios.value?.isNotEmpty() == true) {
            Log.d("ComentariosVM", "Ya tenemos comentarios para tmdbId=$tmdbId, tipo=$tipo")
            return
        }

        lastLoadedTmdbId = tmdbId
        lastLoadedTipo = tipo

        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        apiService.getComentariosByTmdbId(tmdbId, tipo).enqueue(object : Callback<List<Comentario>> {
            override fun onResponse(call: Call<List<Comentario>>, response: Response<List<Comentario>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (tmdbId == lastLoadedTmdbId && tipo == lastLoadedTipo) {
                        val comentariosOrdenados = (response.body() ?: emptyList()).sortedWith(
                            compareByDescending<Comentario> { it.usuario?.rol == "critico" }
                            .thenByDescending { it.createdAt }
                        )
                        _comentarios.value = comentariosOrdenados
                        Log.d("ComentariosVM", "Comentarios cargados para tmdbId=$tmdbId, tipo=$tipo: ${_comentarios.value?.size}")
                    } else {
                        Log.d("ComentariosVM", "Se descartó la respuesta porque ya no corresponde a la película/serie actual")
                    }
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

                    if (nuevoComentario != null) {
                        if (tmdbId == lastLoadedTmdbId && tipo == lastLoadedTipo) {
                            val currentList = _comentarios.value ?: emptyList()
                            _comentarios.value = listOf(nuevoComentario) + currentList
                            _comentarioCreado.value = nuevoComentario
                        }
                    } else {
                        _error.value = "Error: No se recibió datos del comentario"
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        _error.value = "Error: ${response.code()} - $errorBody"
                        Log.e("ComentariosVM", "Error al crear comentario: ${response.code()} - $errorBody")
                    } catch (e: Exception) {
                        _error.value = "Error: ${response.code()}"
                        Log.e("ComentariosVM", "Error al crear comentario: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<Comentario>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error de conexión: ${t.message}"
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

    fun likeComentario(userId: Int, comentarioId: Int, tipo: String, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        val request = LikeComentarioRequest(userId, comentarioId, tipo)

        apiService.likeComentario(request).enqueue(object : Callback<com.example.critflix.model.LikeResponse> {
            override fun onResponse(call: Call<com.example.critflix.model.LikeResponse>, response: Response<com.example.critflix.model.LikeResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val likeResponse = response.body()

                    if (likeResponse != null) {
                        val currentList = _comentarios.value?.toMutableList() ?: mutableListOf()
                        val index = currentList.indexOfFirst { it.id == comentarioId }

                        if (index != -1) {
                            val userLikeStatus = when {
                                likeResponse.message.contains("eliminado") -> "none"
                                else -> tipo
                            }

                            val updatedComentario = likeResponse.comentario.copy(userLikeStatus = userLikeStatus)
                            currentList[index] = updatedComentario
                            _comentarios.value = currentList
                        }
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        _error.value = "Error: ${response.code()} - $errorBody"
                        Log.e("ComentariosVM", "Error al dar like: ${response.code()} - $errorBody")
                    } catch (e: Exception) {
                        _error.value = "Error: ${response.code()}"
                        Log.e("ComentariosVM", "Error al dar like: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<com.example.critflix.model.LikeResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error de conexión: ${t.message}"
                Log.e("ComentariosVM", "Error al dar like: ${t.message}")
            }
        })
    }

    fun loadLikeStatuses(comentarios: List<Comentario>, userId: Int, token: String) {
        if (userId <= 0) return

        val apiService = RetrofitClient.getApiService(token)

        val updatedComentarios = comentarios.map { it.copy() }.toMutableList()
        var completedRequests = 0

        for ((index, comentario) in comentarios.withIndex()) {
            if (comentario.userLikeStatus != null && comentario.userLikeStatus != "none") {
                completedRequests++
                continue
            }

            apiService.getLikeStatus(comentario.id, userId).enqueue(object : Callback<LikeStatusResponse> {
                override fun onResponse(call: Call<LikeStatusResponse>, response: Response<LikeStatusResponse>) {
                    if (response.isSuccessful) {
                        val statusResponse = response.body()
                        if (statusResponse != null) {
                            if (comentario.tmdbId == lastLoadedTmdbId && comentario.tipo == lastLoadedTipo) {
                                val status = statusResponse.status ?: "none"

                                updatedComentarios[index] = comentario.copy(userLikeStatus = status)

                                completedRequests++

                                if (completedRequests == comentarios.size) {
                                    _comentarios.postValue(updatedComentarios)
                                }
                            }
                        }
                    } else {
                        completedRequests++
                    }
                }

                override fun onFailure(call: Call<LikeStatusResponse>, t: Throwable) {
                    Log.e("ComentariosVM", "Error al cargar estado de like: ${t.message}")
                    completedRequests++
                }
            })
        }

        if (completedRequests == comentarios.size) {
            return
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        clearComentarios()
    }
}
