package com.example.critflix.api


class Repository {
    val apiInterface = APIInterface.create()

    suspend fun getAllPelis() = apiInterface.getPelis()
}