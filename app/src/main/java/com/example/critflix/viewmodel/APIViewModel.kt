package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.Repository
import kotlinx.coroutines.launch
import com.example.critflix.model.PelisPopulares
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class APIViewModel : ViewModel() {
    private val repository = Repository()
    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading
    private val _pelis = MutableLiveData<List<PelisPopulares>>()
    val pelis: LiveData<List<PelisPopulares>> = _pelis

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getPelis(totalMoviesNeeded: Int = 50) {
        _loading.value = true

        val numberOfPages = (totalMoviesNeeded + 19) / 20

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movies = repository.getMultiplePages(startPage = 1, numberOfPages = numberOfPages)
                    .take(totalMoviesNeeded)
                withContext(Dispatchers.Main) {
                    _pelis.value = movies
                    _loading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = e.message ?: "Error desconocido"
                    _loading.value = false
                }
            }
        }
    }
}
