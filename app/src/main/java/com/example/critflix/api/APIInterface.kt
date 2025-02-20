package com.example.critflix.api

import com.example.critflix.model.Data
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("movie/popular")
    suspend fun getPelis(
        @Query("api_key") apiKey: String = "42b10f869a4ae1b57a4acf356320b942",
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int
    ): Response<Data>

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(): APIInterface {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(APIInterface::class.java)
        }
    }
}