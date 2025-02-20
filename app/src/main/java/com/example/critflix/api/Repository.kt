package com.example.critflix.api

import com.example.critflix.model.PelisPopulares


class Repository {
    private val apiInterface = APIInterface.create()

    suspend fun getAllPelis(page: Int) = apiInterface.getPelis(page = page)

    suspend fun getMultiplePages(startPage: Int = 1, numberOfPages: Int): List<PelisPopulares> {
        val allMovies = mutableListOf<PelisPopulares>()

        for (page in startPage..(startPage + numberOfPages - 1)) {
            try {
                val response = getAllPelis(page)
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        allMovies.addAll(movies)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return allMovies
    }
}