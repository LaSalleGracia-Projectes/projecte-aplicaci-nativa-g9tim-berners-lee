package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critflix.api.Repository
import com.example.critflix.model.MovieCredits
import com.example.critflix.model.TvCredits
import kotlinx.coroutines.launch

class RepartoViewModel: ViewModel() {
    private val repository = Repository()

    private val _movieCredits = MutableLiveData<MovieCredits>()
    val movieCredits: LiveData<MovieCredits> = _movieCredits

    private val _tvCredits = MutableLiveData<TvCredits>()
    val tvCredits: LiveData<TvCredits> = _tvCredits

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getMovieCredits(movieId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getMovieCredits(movieId)
                if (response.isSuccessful) {
                    _movieCredits.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTvCredits(tvId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getTvCredits(tvId)
                if (response.isSuccessful) {
                    _tvCredits.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}