package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.RespuestaComentario
import com.example.critflix.model.RespuestaComentarioRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RespuestasViewModel : ViewModel() {

    private val _respuestas = MutableLiveData<Map<Int, List<RespuestaComentario>>>()
    val respuestas: LiveData<Map<Int, List<RespuestaComentario>>> = _respuestas

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _respuestaCreada = MutableLiveData<RespuestaComentario?>()
    val respuestaCreada: LiveData<RespuestaComentario?> = _respuestaCreada

    init {
        _respuestas.value = mapOf()
    }

    fun clearRespuestas() {
        _respuestas.value = mapOf()
        _error.value = null
        _respuestaCreada.value = null
    }

    fun getRespuestasByComentarioId(comentarioId: Int, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        apiService.getRespuestasByComentarioId(comentarioId).enqueue(object : Callback<List<RespuestaComentario>> {
            override fun onResponse(call: Call<List<RespuestaComentario>>, response: Response<List<RespuestaComentario>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val respuestasList = response.body() ?: emptyList()

                    val currentRespuestas = _respuestas.value?.toMutableMap() ?: mutableMapOf()
                    currentRespuestas[comentarioId] = respuestasList
                    _respuestas.value = currentRespuestas

                    Log.d("RespuestasVM", "Respuestas cargadas para comentarioId=$comentarioId: ${respuestasList.size}")
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("RespuestasVM", "Error al cargar respuestas: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<RespuestaComentario>>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("RespuestasVM", "Error al cargar respuestas: ${t.message}")
            }
        })
    }

    fun createRespuesta(
        comentarioId: Int,
        userId: Int,
        respuesta: String,
        esSpoiler: Boolean = false,
        token: String
    ) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        val request = RespuestaComentarioRequest(comentarioId, userId, respuesta, esSpoiler)

        apiService.createRespuesta(request).enqueue(object : Callback<RespuestaComentario> {
            override fun onResponse(call: Call<RespuestaComentario>, response: Response<RespuestaComentario>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val nuevaRespuesta = response.body()

                    if (nuevaRespuesta != null) {
                        val currentRespuestas = _respuestas.value?.toMutableMap() ?: mutableMapOf()
                        val respuestasList = currentRespuestas[comentarioId]?.toMutableList() ?: mutableListOf()

                        respuestasList.add(nuevaRespuesta)
                        currentRespuestas[comentarioId] = respuestasList

                        _respuestas.value = currentRespuestas
                        _respuestaCreada.value = nuevaRespuesta

                        Log.d("RespuestasVM", "Respuesta creada para comentarioId=$comentarioId")
                    } else {
                        _error.value = "Error: No se recibió datos de la respuesta"
                        Log.e("RespuestasVM", "Error: No se recibió datos de la respuesta")
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        _error.value = "Error: ${response.code()} - $errorBody"
                        Log.e("RespuestasVM", "Error al crear respuesta: ${response.code()} - $errorBody")
                    } catch (e: Exception) {
                        _error.value = "Error: ${response.code()}"
                        Log.e("RespuestasVM", "Error al crear respuesta: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaComentario>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error de conexión: ${t.message}"
                Log.e("RespuestasVM", "Error al crear respuesta: ${t.message}")
            }
        })
    }

    fun deleteRespuesta(respuestaId: Int, comentarioId: Int, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        apiService.deleteRespuesta(respuestaId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val currentRespuestas = _respuestas.value?.toMutableMap() ?: mutableMapOf()
                    val respuestasList = currentRespuestas[comentarioId]?.toMutableList()

                    if (respuestasList != null) {
                        val nuevaLista = respuestasList.filter { it.id != respuestaId }
                        currentRespuestas[comentarioId] = nuevaLista
                        _respuestas.value = currentRespuestas

                        Log.d("RespuestasVM", "Respuesta eliminada: id=$respuestaId")
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("RespuestasVM", "Error al eliminar respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("RespuestasVM", "Error al eliminar respuesta: ${t.message}")
            }
        })
    }

    fun clearError() {
        _error.value = null
    }

    fun clearRespuestaCreada() {
        _respuestaCreada.value = null
    }
}