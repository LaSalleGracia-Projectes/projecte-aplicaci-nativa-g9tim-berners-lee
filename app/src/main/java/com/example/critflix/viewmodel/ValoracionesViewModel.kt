package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValoracionesViewModel : ViewModel() {
    private val _favoritos = MutableLiveData<List<Int>>()
    val favoritos: LiveData<List<Int>> = _favoritos

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _favoritoStatusMap = MutableLiveData<MutableMap<Int, Boolean>>(mutableMapOf())
    val favoritoStatusMap: LiveData<MutableMap<Int, Boolean>> = _favoritoStatusMap

    private val valoracionesIdMap = mutableMapOf<Int, Int>()

    // Cargar todos los favoritos del usuario
    fun loadUserFavorites(userId: Int, token: String) {
        _loading.value = true
        _error.value = null

        val apiService = RetrofitClient.getApiService(token)

        apiService.getUserFavorites(userId).enqueue(object : Callback<List<ValoracionResponse>> {
            override fun onResponse(call: Call<List<ValoracionResponse>>, response: Response<List<ValoracionResponse>>) {
                _loading.value = false

                if (response.isSuccessful) {
                    val favoritosIds = response.body()?.map { it.id_pelicula } ?: emptyList()
                    _favoritos.value = favoritosIds

                    response.body()?.forEach { valoracion ->
                        valoracionesIdMap[valoracion.id_pelicula] = valoracion.id
                    }

                    val newMap = _favoritoStatusMap.value?.toMutableMap() ?: mutableMapOf()
                    favoritosIds.forEach { id ->
                        newMap[id] = true
                    }
                    _favoritoStatusMap.value = newMap

                    Log.d("ValoracionesViewModel", "Favoritos cargados: $favoritosIds")
                } else {
                    _error.value = "Error al cargar favoritos: ${response.code()}"
                    Log.e("ValoracionesViewModel", "Error al cargar favoritos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ValoracionResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = "Error de conexión: ${t.message}"
                Log.e("ValoracionesViewModel", "Error de conexión", t)
            }
        })
    }

    // Verificar si una película es favorita
    fun checkFavoriteStatus(userId: Int, peliculaId: Int, token: String) {
        val apiService = RetrofitClient.getApiService(token)

        apiService.checkFavoriteStatus(userId, peliculaId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isFavorite = response.body() ?: false
                    val newMap = _favoritoStatusMap.value?.toMutableMap() ?: mutableMapOf()
                    newMap[peliculaId] = isFavorite
                    _favoritoStatusMap.value = newMap

                    if (isFavorite) {
                        val currentFavorites = _favoritos.value?.toMutableList() ?: mutableListOf()
                        if (!currentFavorites.contains(peliculaId)) {
                            currentFavorites.add(peliculaId)
                            _favoritos.value = currentFavorites
                        }

                        apiService.getUserFavorites(userId).enqueue(object : Callback<List<ValoracionResponse>> {
                            override fun onResponse(call: Call<List<ValoracionResponse>>, response: Response<List<ValoracionResponse>>) {
                                if (response.isSuccessful) {
                                    response.body()?.forEach { valoracion ->
                                        if (valoracion.id_pelicula == peliculaId) {
                                            valoracionesIdMap[peliculaId] = valoracion.id
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<List<ValoracionResponse>>, t: Throwable) {
                                Log.e("ValoracionesViewModel", "Error al obtener ID de valoración", t)
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("ValoracionesViewModel", "Error al verificar favorito", t)
            }
        })
    }

    // Marcar como favorito
    fun addFavorite(userId: Int, peliculaId: Int, token: String, onComplete: (Boolean) -> Unit) {
        _loading.value = true
        _error.value = null

        val request = ValoracionRequest(
            user_id = userId,
            id_pelicula = peliculaId
        )

        val apiService = RetrofitClient.getApiService(token)

        apiService.addFavorite(request).enqueue(object : Callback<ValoracionResponse> {
            override fun onResponse(call: Call<ValoracionResponse>, response: Response<ValoracionResponse>) {
                _loading.value = false

                if (response.isSuccessful) {
                    val currentFavorites = _favoritos.value?.toMutableList() ?: mutableListOf()
                    if (!currentFavorites.contains(peliculaId)) {
                        currentFavorites.add(peliculaId)
                        _favoritos.value = currentFavorites
                    }

                    response.body()?.let { valoracion ->
                        valoracionesIdMap[peliculaId] = valoracion.id
                    }

                    val newMap = _favoritoStatusMap.value?.toMutableMap() ?: mutableMapOf()
                    newMap[peliculaId] = true
                    _favoritoStatusMap.value = newMap

                    Log.d("ValoracionesViewModel", "Añadido a favoritos: $peliculaId")
                    onComplete(true)
                } else {
                    val errorMsg = "Error al añadir favorito: ${response.code()}"
                    _error.value = errorMsg
                    Log.e("ValoracionesViewModel", errorMsg)

                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ValoracionesViewModel", "Error body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("ValoracionesViewModel", "Error reading error body", e)
                    }

                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<ValoracionResponse>, t: Throwable) {
                _loading.value = false
                _error.value = "Error de conexión: ${t.message}"
                Log.e("ValoracionesViewModel", "Error de conexión", t)
                onComplete(false)
            }
        })
    }

    // Quitar de favoritos
    fun removeFavorite(valoracionId: Int, peliculaId: Int, token: String, onComplete: (Boolean) -> Unit) {
        _loading.value = true

        val apiService = RetrofitClient.getApiService(token)

        apiService.removeFavorite(valoracionId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _loading.value = false

                if (response.isSuccessful) {
                    val currentFavorites = _favoritos.value?.toMutableList() ?: mutableListOf()
                    currentFavorites.remove(peliculaId)
                    _favoritos.value = currentFavorites

                    val newMap = _favoritoStatusMap.value?.toMutableMap() ?: mutableMapOf()
                    newMap[peliculaId] = false
                    _favoritoStatusMap.value = newMap

                    valoracionesIdMap.remove(peliculaId)

                    Log.d("ValoracionesViewModel", "Eliminado de favoritos: $peliculaId")
                    onComplete(true)
                } else {
                    _error.value = "Error al eliminar favorito: ${response.code()}"
                    Log.e("ValoracionesViewModel", "Error al eliminar favorito: ${response.code()}")
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _loading.value = false
                _error.value = "Error de conexión: ${t.message}"
                Log.e("ValoracionesViewModel", "Error de conexión", t)
                onComplete(false)
            }
        })
    }

    // Alternar estado de favorito
    fun toggleFavorite(userId: Int, peliculaId: Int, token: String, onComplete: (Boolean) -> Unit) {
        val isFavorite = _favoritoStatusMap.value?.get(peliculaId) ?: false

        if (isFavorite) {
            val valoracionId = valoracionesIdMap[peliculaId]
            if (valoracionId != null) {
                removeFavorite(valoracionId, peliculaId, token, onComplete)
            } else {
                val apiService = RetrofitClient.getApiService(token)
                apiService.getUserFavorites(userId).enqueue(object : Callback<List<ValoracionResponse>> {
                    override fun onResponse(call: Call<List<ValoracionResponse>>, response: Response<List<ValoracionResponse>>) {
                        if (response.isSuccessful) {
                            val valoracion = response.body()?.find { it.id_pelicula == peliculaId }
                            if (valoracion != null) {
                                valoracionesIdMap[peliculaId] = valoracion.id
                                removeFavorite(valoracion.id, peliculaId, token, onComplete)
                            } else {
                                _error.value = "Error: No se encontró el ID de la valoración"
                                onComplete(false)
                            }
                        } else {
                            _error.value = "Error al obtener valoraciones: ${response.code()}"
                            onComplete(false)
                        }
                    }

                    override fun onFailure(call: Call<List<ValoracionResponse>>, t: Throwable) {
                        _error.value = "Error de conexión: ${t.message}"
                        onComplete(false)
                    }
                })
            }
        } else {
            addFavorite(userId, peliculaId, token, onComplete)
        }
    }

    // Limpiar errores
    fun clearError() {
        _error.value = null
    }
}