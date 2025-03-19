package com.example.critflix.api

import com.example.critflix.model.Data
import com.example.critflix.model.DataSeries
import com.example.critflix.model.MovieCredits
import com.example.critflix.model.TvCredits
import com.example.critflix.model.Generos
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIInterface {
    @GET("movie/popular")
    suspend fun getPelis(
        @Query("api_key") apiKey: String = "42b10f869a4ae1b57a4acf356320b942",
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int
    ): Response<Data>

    @GET("tv/popular")
    suspend fun getSeries(
        @Query("api_key") apiKey: String = "42b10f869a4ae1b57a4acf356320b942",
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int
    ): Response<DataSeries>

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String = "42b10f869a4ae1b57a4acf356320b942",
        @Query("language") language: String = "es-ES"
    ): Response<Generos>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = "42b10f869a4ae1b57a4acf356320b942",
        @Query("language") language: String = "es-ES"
    ): Response<MovieCredits>

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String = "42b10f869a4ae1b57a4acf356320b942",
        @Query("language") language: String = "es-ES"
    ): Response<TvCredits>

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