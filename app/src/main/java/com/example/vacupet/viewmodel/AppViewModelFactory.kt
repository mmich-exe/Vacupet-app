package com.example.vacupet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vacupet.repository.AppRepository

/**
 * AppViewModel necesita un Repository y el uid del usuario en su constructor,
 * y Android no sabe construirlo solo. Esta fábrica le indica cómo hacerlo.
 */
class AppViewModelFactory(
    private val repository: AppRepository,
    private val userId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AppViewModel::class.java)) {
            "ViewModel desconocido: ${modelClass.name}"
        }
        return AppViewModel(repository, userId) as T
    }
}
