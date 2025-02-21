package com.example.critflix.api

import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares

class Repository {
    private val apiInterface = APIInterface.create()

    suspend fun getAllPelis(page: Int) = apiInterface.getPelis(page = page)
    suspend fun getAllSeries(page: Int) = apiInterface.getSeries(page = page)

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
}