package com.example.critflix.api
import com.example.critflix.model.PelisPopulares
import okhttp3.Response


class Repository {
    val apiInterface = APIInterface.create()

    suspend fun getAllPelis() = apiInterface.getPelis()

}