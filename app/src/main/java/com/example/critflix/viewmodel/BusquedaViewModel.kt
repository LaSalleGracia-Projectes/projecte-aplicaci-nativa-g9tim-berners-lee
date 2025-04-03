package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares

// Enum para los tipos de contenido
enum class ContentType {
    ALL, MOVIES, SERIES
}

// Enum para criterios de ordenación
enum class SortCriteria {
    ALPHABETICAL, RELEASE_DATE, POPULARITY, RATING
}

// Enum para dirección de ordenación
enum class SortDirection {
    ASCENDING, DESCENDING
}

class BusquedaViewModel : ViewModel() {

    //Barra de busqueda
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _isSearchActive = MutableLiveData<Boolean>(false)
    val isSearchActive: LiveData<Boolean> = _isSearchActive

    private val _filteredPeliculas = MutableLiveData<List<PelisPopulares>>(emptyList())
    val filteredPeliculas: LiveData<List<PelisPopulares>> = _filteredPeliculas

    private val _filteredSeries = MutableLiveData<List<SeriesPopulares>>(emptyList())
    val filteredSeries: LiveData<List<SeriesPopulares>> = _filteredSeries

    // Filtros y ordenación
    private val _contentType = MutableLiveData<ContentType>(ContentType.ALL)
    val contentType: LiveData<ContentType> = _contentType

    private val _sortCriteria = MutableLiveData<SortCriteria>(SortCriteria.POPULARITY)
    val sortCriteria: LiveData<SortCriteria> = _sortCriteria

    private val _sortDirection = MutableLiveData<SortDirection>(SortDirection.DESCENDING)
    val sortDirection: LiveData<SortDirection> = _sortDirection

    private val _showFilterDialog = MutableLiveData<Boolean>(false)
    val showFilterDialog: LiveData<Boolean> = _showFilterDialog

    fun updateSearchQuery(query: String, peliculas: List<PelisPopulares>, series: List<SeriesPopulares>) {
        _searchQuery.value = query
        _isSearchActive.value = query.isNotBlank()

        if (query.isBlank()) {
            _filteredPeliculas.value = emptyList()
            _filteredSeries.value = emptyList()
        } else {
            applyFiltersAndSort(query, peliculas, series)
        }
    }

    // Limpiar la búsqueda actual
    fun clearSearch() {
        _searchQuery.value = ""
        _isSearchActive.value = false
        _filteredPeliculas.value = emptyList()
        _filteredSeries.value = emptyList()
    }

    // Aplicar filtros y ordenación
    fun applyFiltersAndSort(query: String = _searchQuery.value ?: "", peliculas: List<PelisPopulares>, series: List<SeriesPopulares>) {
        // Si no hay búsqueda activa, no hacemos nada
        if (query.isBlank() && !_isSearchActive.value!!) {
            return
        }

        var filteredMovies = emptyList<PelisPopulares>()
        var filteredShows = emptyList<SeriesPopulares>()

        // Aplicar filtro de tipo de contenido y búsqueda
        when (_contentType.value) {
            ContentType.ALL, null -> {
                filteredMovies = peliculas.filter { it.title.contains(query, ignoreCase = true) }
                filteredShows = series.filter { it.name.contains(query, ignoreCase = true) }
            }
            ContentType.MOVIES -> {
                filteredMovies = peliculas.filter { it.title.contains(query, ignoreCase = true) }
            }
            ContentType.SERIES -> {
                filteredShows = series.filter { it.name.contains(query, ignoreCase = true) }
            }
        }

        // Aplicar ordenación a películas
        filteredMovies = when (_sortCriteria.value) {
            SortCriteria.ALPHABETICAL -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredMovies.sortedBy { it.title }
                } else {
                    filteredMovies.sortedByDescending { it.title }
                }
            }
            SortCriteria.RELEASE_DATE -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredMovies.sortedBy { it.release_date }
                } else {
                    filteredMovies.sortedByDescending { it.release_date }
                }
            }
            SortCriteria.RATING -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredMovies.sortedBy { it.vote_average }
                } else {
                    filteredMovies.sortedByDescending { it.vote_average }
                }
            }
            SortCriteria.POPULARITY, null -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredMovies.sortedBy { it.popularity }
                } else {
                    filteredMovies.sortedByDescending { it.popularity }
                }
            }
        }

        // Aplicar ordenación a series
        filteredShows = when (_sortCriteria.value) {
            SortCriteria.ALPHABETICAL -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredShows.sortedBy { it.name }
                } else {
                    filteredShows.sortedByDescending { it.name }
                }
            }
            SortCriteria.RELEASE_DATE -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredShows.sortedBy { it.first_air_date }
                } else {
                    filteredShows.sortedByDescending { it.first_air_date }
                }
            }
            SortCriteria.RATING -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredShows.sortedBy { it.vote_average }
                } else {
                    filteredShows.sortedByDescending { it.vote_average }
                }
            }
            SortCriteria.POPULARITY, null -> {
                if (_sortDirection.value == SortDirection.ASCENDING) {
                    filteredShows.sortedBy { it.popularity }
                } else {
                    filteredShows.sortedByDescending { it.popularity }
                }
            }
        }
        _filteredPeliculas.value = filteredMovies
        _filteredSeries.value = filteredShows
    }

    // Mostrar/ocultar diálogo de filtros
    fun toggleFilterDialog() {
        _showFilterDialog.value = !(_showFilterDialog.value ?: false)
    }

    // Actualizar tipo de contenido
    fun updateContentType(type: ContentType) {
        _contentType.value = type
    }

    // Actualizar criterio de ordenación
    fun updateSortCriteria(criteria: SortCriteria) {
        _sortCriteria.value = criteria
    }

    // Actualizar dirección de ordenación
    fun updateSortDirection(direction: SortDirection) {
        _sortDirection.value = direction
    }
}