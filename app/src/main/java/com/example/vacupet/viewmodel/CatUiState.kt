package com.example.vacupet.viewmodel

import com.example.vacupet.data.model.CatBreed

data class CatUiState(
    val breeds: List<CatBreed> = emptyList(),
    val filteredBreeds: List<CatBreed> = emptyList(),
    val searchQuery: String = "",
    val apiKey: String = "",
    val showApiKeyInput: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
) {
    fun breedPorId(id: String): CatBreed? = breeds.find { it.id == id }
}
