package com.example.vacupet.viewmodel

/** Estado de autenticación. [uid] != null significa que hay una sesión activa. */
data class AuthUiState(
    val uid: String? = null,
    val email: String? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)
