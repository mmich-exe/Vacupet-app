package com.example.vacupet.repository

import com.example.vacupet.data.model.Breed
import com.example.vacupet.data.network.TheDogApiService

class DogRepository(
    private val apiService: TheDogApiService = TheDogApiService.create()
) {
    companion object {
        // ====================================================================
        // Coloca tu API Key de TheDogAPI aquí entre las comillas:
        // ====================================================================
        private const val DEFAULT_API_KEY = "live_hFPoCLPqgRDY119hjpJsqugtThZ8KhLCjHBKC6uYP1RlhIaP73AUmIWKKRwMzEZE"
    }

    suspend fun getBreeds(apiKey: String = ""): Result<List<Breed>> {
        val keyToUse = if (apiKey.isNotBlank()) apiKey else DEFAULT_API_KEY
        return try {
            val breeds = apiService.getBreeds(keyToUse)
            Result.success(breeds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
