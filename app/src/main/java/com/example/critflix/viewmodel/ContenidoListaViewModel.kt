package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critflix.api.APIInterface
import com.example.critflix.api.Repository
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.ContenidoLista
import com.example.critflix.model.ContenidoListaRequest
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContenidoListaViewModel : ViewModel() {
    private val _contentItems = MutableLiveData<List<ContenidoLista>>()
    val contentItems: LiveData<List<ContenidoLista>> = _contentItems

    private val _contentState = MutableLiveData<ContentState>(ContentState.Idle)
    val contentState: LiveData<ContentState> = _contentState

    private val _movies = MutableLiveData<Map<Int, PelisPopulares>>()
    val movies: LiveData<Map<Int, PelisPopulares>> = _movies

    private val _series = MutableLiveData<Map<Int, SeriesPopulares>>()
    val series: LiveData<Map<Int, SeriesPopulares>> = _series

    private val apiTMDb = APIInterface.create()

    init {
        _contentItems.value = emptyList()
        _movies.value = emptyMap()
        _series.value = emptyMap()
    }

    fun loadListContent(listaId: String, token: String) {
        _contentState.value = ContentState.Loading
        val apiService = RetrofitClient.getApiService(token)

        apiService.getListContent(listaId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body()
                        val dataList = responseBody?.get("data") as? List<*>

                        if (dataList != null) {
                            val contentItems = dataList.mapNotNull { item ->
                                if (item is Map<*, *>) {
                                    val contentItem = item as Map<String, Any>
                                    ContenidoLista.fromApiResponse(contentItem)
                                } else null
                            }
                            _contentItems.value = contentItems
                            _contentState.value = ContentState.Success

                            loadContentDetails(contentItems)
                        }
                    } catch (e: Exception) {
                        Log.e("ContenidoListaViewModel", "Error parsing response", e)
                        _contentState.value = ContentState.Error("Error al procesar la respuesta")
                    }
                } else {
                    Log.e("ContenidoListaViewModel", "Error loading content: ${response.code()}")
                    _contentState.value = ContentState.Error("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("ContenidoListaViewModel", "Error loading content", t)
                _contentState.value = ContentState.Error("Error de conexión: ${t.message}")
            }
        })
    }

    fun addContentToList(listaId: String, tmdbId: Int, tipo: String, token: String) {
        _contentState.value = ContentState.Loading

        val request = ContenidoListaRequest(
            id_lista = listaId,
            tmdb_id = tmdbId,
            tipo = tipo
        )

        val apiService = RetrofitClient.getApiService(token)

        apiService.addContentToList(request).enqueue(object : Callback<ContenidoLista> {
            override fun onResponse(call: Call<ContenidoLista>, response: Response<ContenidoLista>) {
                if (response.isSuccessful && response.body() != null) {
                    val newContent = response.body()!!
                    val currentContent = _contentItems.value ?: emptyList()
                    _contentItems.value = currentContent + newContent
                    _contentState.value = ContentState.Success

                    loadContentDetails(listOf(newContent))
                } else {
                    _contentState.value = ContentState.Error("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ContenidoLista>, t: Throwable) {
                _contentState.value = ContentState.Error("Error de conexión: ${t.message}")
            }
        })
    }

    fun removeContentFromList(contentId: String, token: String) {
        _contentState.value = ContentState.Loading

        val apiService = RetrofitClient.getApiService(token)

        apiService.removeContentFromList(contentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val currentContent = _contentItems.value ?: emptyList()
                    _contentItems.value = currentContent.filter { it.id != contentId }
                    _contentState.value = ContentState.Success
                } else {
                    _contentState.value = ContentState.Error("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _contentState.value = ContentState.Error("Error de conexión: ${t.message}")
            }
        })
    }

    private val repository = Repository()

    private fun loadContentDetails(contentItems: List<ContenidoLista>) {
        viewModelScope.launch {
            try {
                val movieDetails = contentItems
                    .filter { it.tipo == "pelicula" }
                    .associate { content ->
                        val details = try {
                            repository.getMovieDetails(content.tmdb_id).body()
                        } catch (e: Exception) {
                            Log.e("ContenidoListaVM", "Error loading movie details", e)
                            null
                        }
                        content.tmdb_id to details
                    }
                    .filterValues { it != null } as Map<Int, PelisPopulares>

                val seriesDetails = contentItems
                    .filter { it.tipo == "serie" }
                    .associate { content ->
                        val details = try {
                            repository.getTvDetails(content.tmdb_id).body()
                        } catch (e: Exception) {
                            Log.e("ContenidoListaVM", "Error loading TV details", e)
                            null
                        }
                        content.tmdb_id to details
                    }
                    .filterValues { it != null } as Map<Int, SeriesPopulares>

                _movies.value = movieDetails
                _series.value = seriesDetails
                _contentState.value = ContentState.Success
            } catch (e: Exception) {
                Log.e("ContenidoListaVM", "Error loading content details", e)
                _contentState.value = ContentState.Error("Error cargando detalles")
            }
        }
    }
}

sealed class ContentState {
    object Idle : ContentState()
    object Loading : ContentState()
    object Success : ContentState()
    data class Error(val message: String) : ContentState()
}