package com.example.vacupet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacupet.data.model.CatBreed
import com.example.vacupet.repository.CatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatViewModel(
    private val repository: CatRepository = CatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatUiState())
    val uiState: StateFlow<CatUiState> = _uiState.asStateFlow()

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
                            error = "Error: ${err.localizedMessage}",
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

    private fun filtrar(list: List<CatBreed>, query: String): List<CatBreed> {
        if (query.isBlank()) return list
        return list.filter {
            it.name.contains(query, ignoreCase = true) ||
                    (it.temperament?.contains(query, ignoreCase = true) == true) ||
                    (it.origin?.contains(query, ignoreCase = true) == true)
        }
    }
}
