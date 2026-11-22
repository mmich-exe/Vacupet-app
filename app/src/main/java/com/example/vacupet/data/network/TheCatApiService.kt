package com.example.vacupet.data.network

import com.example.vacupet.data.model.CatBreed
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface TheCatApiService {
    @GET("v1/breeds")
    suspend fun getBreeds(
        @Header("x-api-key") apiKey: String = ""
    ): List<CatBreed>

    companion object {
        private const val BASE_URL = "https://api.thecatapi.com/"

        fun create(): TheCatApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TheCatApiService::class.java)
        }
    }
}
