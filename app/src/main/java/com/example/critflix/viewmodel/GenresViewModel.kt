package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.Repository
import com.example.critflix.model.Genre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenresViewModel : ViewModel() {
    private val repository = Repository()

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Mapa para acceder a los nombres de géneros por ID
    private val _genreMap = MutableLiveData<Map<Int, String>>()
    val genreMap: LiveData<Map<Int, String>> = _genreMap

    init {
        loadGenres()
    }

    fun loadGenres() {
        _loading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getMovieGenres()
                if (response.isSuccessful) {
                    response.body()?.let { generos ->
                        // Crear el mapa de ID a nombre de género
                        val map = mutableMapOf<Int, String>()
                        generos.genres.forEach { genre ->
                            map[genre.id] = genre.name
                        }

                        withContext(Dispatchers.Main) {
                            _genres.value = generos.genres
                            _genreMap.value = map
                            _loading.value = false
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _error.value = "Error al cargar los géneros: ${response.message()}"
                        _loading.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Error al cargar los géneros: ${e.message}"
                    _loading.value = false
                }
            }
        }
    }

    // Obtener los nombres de géneros para una película
    fun getGenreNames(genreIds: List<Int>): List<String> {
        val genreNames = mutableListOf<String>()
        val currentMap = _genreMap.value ?: emptyMap()

        genreIds.forEach { id ->
            currentMap[id]?.let { name ->
                genreNames.add(name)
            }
        }

        return genreNames
    }

    // Obtener géneros como texto formateado
    fun getGenresFormattedText(genreIds: List<Int>): String {
        val names = getGenreNames(genreIds)
        return names.joinToString(", ")
    }
}