package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares

class BusquedaViewModel : ViewModel() {
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _isSearchActive = MutableLiveData<Boolean>(false)
    val isSearchActive: LiveData<Boolean> = _isSearchActive

    private val _filteredPeliculas = MutableLiveData<List<PelisPopulares>>(emptyList())
    val filteredPeliculas: LiveData<List<PelisPopulares>> = _filteredPeliculas

    private val _filteredSeries = MutableLiveData<List<SeriesPopulares>>(emptyList())
    val filteredSeries: LiveData<List<SeriesPopulares>> = _filteredSeries

    fun updateSearchQuery(query: String, peliculas: List<PelisPopulares>, series: List<SeriesPopulares>) {
        _searchQuery.value = query
        _isSearchActive.value = query.isNotBlank()

        if (query.isBlank()) {
            _filteredPeliculas.value = emptyList()
            _filteredSeries.value = emptyList()
        } else {
            filterContent(query, peliculas, series)
        }
    }

    // Funcion para limpiar la busqueda actual
    fun clearSearch() {
        _searchQuery.value = ""
        _isSearchActive.value = false
        _filteredPeliculas.value = emptyList()
        _filteredSeries.value = emptyList()
    }

    // Funcion para filtrar el contenido
    private fun filterContent(query: String, peliculas: List<PelisPopulares>, series: List<SeriesPopulares>) {
        // Filtrar peliculas
        val filteredMovies = peliculas.filter {
            it.title.contains(query, ignoreCase = true)
        }
        _filteredPeliculas.value = filteredMovies

        // Filtrar series
        val filteredShows = series.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _filteredSeries.value = filteredShows
    }

    fun applyFilters() {
        // TODO: Boton de filtros
    }
}