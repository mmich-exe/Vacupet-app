package com.example.vacupet.repository

import com.example.vacupet.data.model.CatBreed
import com.example.vacupet.data.network.TheCatApiService

class CatRepository(
    private val apiService: TheCatApiService = TheCatApiService.create()
) {
    companion object {
        private const val DEFAULT_API_KEY = "live_6B9qOzdBeZ8qsBv8wdxxIo106zBZmeK1kt6L0vFXg6bXZkRqUq2Uefho5s6DecYU"
    }

    suspend fun getBreeds(apiKey: String = ""): Result<List<CatBreed>> {
        val keyToUse = if (apiKey.isNotBlank()) apiKey else DEFAULT_API_KEY
        return try {
            val breeds = apiService.getBreeds(keyToUse)
            Result.success(breeds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
