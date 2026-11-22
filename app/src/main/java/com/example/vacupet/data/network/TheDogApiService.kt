package com.example.vacupet.data.network

import com.example.vacupet.data.model.Breed
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface TheDogApiService {
    @GET("v1/breeds")
    suspend fun getBreeds(
        @Header("x-api-key") apiKey: String = ""
    ): List<Breed>

    companion object {
        private const val BASE_URL = "https://api.thedogapi.com/"

        fun create(): TheDogApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TheDogApiService::class.java)
        }
    }
}
