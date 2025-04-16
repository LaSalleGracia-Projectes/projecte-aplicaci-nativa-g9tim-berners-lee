package com.example.critflix.api

import com.example.critflix.model.MovieCredits
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.model.TvCredits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class Repository {
    private val apiInterface = APIInterface.create()

    suspend fun getAllPelis(page: Int) = apiInterface.getPelis(page = page)
    suspend fun getAllSeries(page: Int) = apiInterface.getSeries(page = page)
    suspend fun getMovieGenres() = apiInterface.getMovieGenres()

    suspend fun getMultiplePages(startPage: Int = 1, numberOfPages: Int): List<PelisPopulares> {
        val allMovies = mutableListOf<PelisPopulares>()

        for (page in startPage..(startPage + numberOfPages - 1)) {
            try {
                val response = getAllPelis(page)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        allMovies.addAll(data.results)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return allMovies
    }

    suspend fun getMultiplePagesSeries(startPage: Int = 1, numberOfPages: Int): List<SeriesPopulares> {
        val allSeries = mutableListOf<SeriesPopulares>()

        for (page in startPage..(startPage + numberOfPages - 1)) {
            try {
                val response = getAllSeries(page)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        allSeries.addAll(data.results)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return allSeries
    }

    suspend fun getMovieCredits(movieId: Int): Response<MovieCredits> {
        return apiInterface.getMovieCredits(movieId = movieId)
    }

    suspend fun getTvCredits(tvId: Int): Response<TvCredits> {
        return apiInterface.getTvCredits(tvId = tvId)
    }

    suspend fun getMovieDetails(movieId: Int): Response<PelisPopulares> {
        return apiInterface.getMovieDetails(movieId = movieId)
    }

    suspend fun getTvDetails(tvId: Int): Response<SeriesPopulares> {
        return apiInterface.getTvDetails(tvId = tvId)
    }
}