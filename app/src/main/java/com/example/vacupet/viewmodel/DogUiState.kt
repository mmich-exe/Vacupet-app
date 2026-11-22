package com.example.vacupet.viewmodel

import com.example.vacupet.data.model.Breed

data class DogUiState(
    val breeds: List<Breed> = emptyList(),
    val filteredBreeds: List<Breed> = emptyList(),
    val searchQuery: String = "",
    val apiKey: String = "",
    val showApiKeyInput: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
) {
    fun breedPorId(id: Int): Breed? = breeds.find { it.id == id }
}
