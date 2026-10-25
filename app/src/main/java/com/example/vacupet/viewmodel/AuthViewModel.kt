package com.example.vacupet.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Controla el acceso a la app con Firebase Authentication (correo y contraseña).
 * Firebase guarda la sesión en el dispositivo, así que al reabrir la app
 * [FirebaseAuth.getCurrentUser] sigue devolviendo al usuario (recuperación de sesión).
 */
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(
        AuthUiState(uid = auth.currentUser?.uid, email = auth.currentUser?.email)
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** ¿Hay una sesión guardada? Se usa para elegir la pantalla inicial. */
    fun haySesion(): Boolean = auth.currentUser != null

    init {
        verificarSesionGuardada()
    }

    /**
     * Recuperación de sesión: Firebase restaura al usuario guardado en el dispositivo.
     * Además se consulta al servidor para confirmar que la cuenta sigue existiendo y activa
     * (si fue eliminada o deshabilitada, se cierra la sesión local). Si no hay internet
     * se conserva la sesión y la app funciona sin conexión.
     */
    private fun verificarSesionGuardada() {
        val usuario = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                usuario.reload().await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: FirebaseAuthInvalidUserException) {
                logout()
            } catch (_: Exception) {
                // Sin conexión u otro error temporal: se mantiene la sesión.
            }
        }
    }

    fun limpiarError() = _uiState.update { it.copy(error = null, mensaje = null) }

    /** Envía un correo para restablecer la contraseña. */
    fun recuperarPassword(email: String) {
        val limpio = email.trim()
        if (limpio.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(limpio).matches()) {
            _uiState.update { it.copy(error = "Escribe tu correo arriba para enviarte el enlace", mensaje = null) }
            return
        }
        _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(limpio).await()
                _uiState.update {
                    it.copy(cargando = false, mensaje = "Te enviamos un correo para restablecer tu contraseña")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = traducirError(e)) }
            }
        }
    }

    fun login(email: String, password: String) {
        val error = validar(email, password)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        autenticar { auth.signInWithEmailAndPassword(email.trim(), password).await() }
    }

    fun registrar(email: String, password: String, confirmacion: String) {
        val error = validar(email, password)
            ?: if (password != confirmacion) "Las contraseñas no coinciden" else null
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        // Al crear la cuenta Firebase inicia sesión automáticamente.
        autenticar { auth.createUserWithEmailAndPassword(email.trim(), password).await() }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState()
    }

    private fun autenticar(accion: suspend () -> com.google.firebase.auth.AuthResult) {
        _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
        viewModelScope.launch {
            try {
                val usuario = accion().user
                _uiState.value = AuthUiState(uid = usuario?.uid, email = usuario?.email)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = traducirError(e)) }
            }
        }
    }

    private fun validar(email: String, password: String): String? = when {
        email.isBlank() -> "Ingresa tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "El correo no es válido"
        password.isBlank() -> "Ingresa tu contraseña"
        password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        else -> null
    }

    private fun traducirError(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil (mínimo 6 caracteres)"
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo"
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
        is FirebaseAuthUserCollisionException -> "Ese correo ya está registrado"
        is FirebaseNetworkException -> "Sin conexión a internet"
        else -> "No se pudo completar la operación. Intenta nuevamente."
    }
}
