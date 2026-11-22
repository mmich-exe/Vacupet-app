package com.example.vacupet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacupet.data.model.Breed
import com.example.vacupet.repository.DogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DogViewModel(
    private val repository: DogRepository = DogRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DogUiState())
    val uiState: StateFlow<DogUiState> = _uiState.asStateFlow()

    init {
        cargarRazas()
    }

    fun cargarRazas() {
        val currentKey = _uiState.value.apiKey
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            repository.getBreeds(currentKey).fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            breeds = list,
                            filteredBreeds = filtrar(list, it.searchQuery),
                            cargando = false,
                            showApiKeyInput = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            error = "Error HTTP o de red: ${err.localizedMessage}\n(TheDogAPI puede requerir una API Key gratuita)",
                            showApiKeyInput = true
                        )
                    }
                }
            )
        }
    }

    fun cambiarApiKey(key: String) {
        _uiState.update { it.copy(apiKey = key) }
    }

    fun cambiarBusqueda(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredBreeds = filtrar(state.breeds, query)
            )
        }
    }

    private fun filtrar(list: List<Breed>, query: String): List<Breed> {
        if (query.isBlank()) return list
        return list.filter {
            it.name.contains(query, ignoreCase = true) ||
                    (it.temperament?.contains(query, ignoreCase = true) == true) ||
                    (it.origin?.contains(query, ignoreCase = true) == true)
        }
    }
}
