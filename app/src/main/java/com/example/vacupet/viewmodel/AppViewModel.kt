package com.example.vacupet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacupet.data.Mascota
import com.example.vacupet.data.Perfil
import com.example.vacupet.data.Recordatorio
import com.example.vacupet.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: AppRepository,
    private val userId: String
) : ViewModel() {

    /** Parte del estado que no viene de la base de datos. */
    private data class Local(
        val busqueda: String = "",
        val especie: String? = null,
        val erroresMascota: MascotaErrores = MascotaErrores(),
        val erroresRecordatorio: RecordatorioErrores = RecordatorioErrores(),
        val erroresPerfil: PerfilErrores = PerfilErrores()
    )

    private val local = MutableStateFlow(Local())

    val uiState: StateFlow<AppUiState> = combine(
        repository.observarMascotas(userId),
        repository.observarRecordatorios(userId),
        repository.observarPerfil(userId),
        local
    ) { mascotas, recordatorios, perfil, l ->
        AppUiState(
            cargando = false,
            mascotas = mascotas,
            recordatorios = recordatorios,
            perfil = perfil,
            busqueda = l.busqueda,
            filtroEspecie = l.especie,
            erroresMascota = l.erroresMascota,
            erroresRecordatorio = l.erroresRecordatorio,
            erroresPerfil = l.erroresPerfil
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    // ---------- Búsqueda y filtro (funcionalidad propia) ----------

    fun cambiarBusqueda(texto: String) = local.update { it.copy(busqueda = texto) }

    fun cambiarFiltroEspecie(especie: String?) = local.update { it.copy(especie = especie) }

    // ---------- Mascotas: validación + CRUD ----------

    fun limpiarErroresMascota() = local.update { it.copy(erroresMascota = MascotaErrores()) }

    /** CREATE (id == null) o UPDATE (id != null). Solo guarda si los datos son válidos. */
    fun guardarMascota(
        id: Int?,
        nombre: String,
        especie: String,
        fotoPath: String?,
        cumpleanos: String,
        vacunas: String,
        infoExtra: String,
        onGuardado: () -> Unit
    ) {
        val errores = MascotaErrores(
            nombre = when {
                nombre.isBlank() -> "El nombre es obligatorio"
                nombre.trim().length < 2 -> "Debe tener al menos 2 caracteres"
                else -> null
            },
            especie = if (especie.isBlank()) "Selecciona una especie" else null
        )
        if (errores.hayErrores) {
            local.update { it.copy(erroresMascota = errores) }
            return
        }
        viewModelScope.launch {
            if (id == null) {
                repository.insertarMascota(
                    Mascota(
                        userId = userId,
                        nombre = nombre.trim(),
                        especie = especie,
                        fotoPath = fotoPath,
                        cumpleanos = cumpleanos,
                        vacunas = vacunas.trim(),
                        infoExtra = infoExtra.trim()
                    )
                )
            } else {
                val actual = repository.obtenerMascota(id, userId) ?: return@launch
                repository.actualizarMascota(
                    actual.copy(
                        nombre = nombre.trim(),
                        especie = especie,
                        fotoPath = fotoPath,
                        cumpleanos = cumpleanos,
                        vacunas = vacunas.trim(),
                        infoExtra = infoExtra.trim()
                    )
                )
            }
            local.update { it.copy(erroresMascota = MascotaErrores()) }
            onGuardado()
        }
    }

    /** UPDATE rápido: marca o desmarca una mascota como favorita. */
    fun alternarFavorito(mascota: Mascota) {
        viewModelScope.launch {
            repository.actualizarMascota(mascota.copy(favorito = !mascota.favorito))
        }
    }

    fun eliminarMascota(mascota: Mascota) {
        viewModelScope.launch { repository.eliminarMascota(mascota) }
    }

    // ---------- Perfil del usuario ----------

    fun limpiarErroresPerfil() = local.update { it.copy(erroresPerfil = PerfilErrores()) }

    /** Crea o actualiza el perfil del usuario (una fila por usuario). */
    fun guardarPerfil(
        nombre: String,
        telefono: String,
        cumpleanos: String,
        ciudad: String,
        fotoPath: String?,
        onGuardado: () -> Unit
    ) {
        val digitos = telefono.filter { it.isDigit() }
        val errores = PerfilErrores(
            nombre = when {
                nombre.isBlank() -> "El nombre es obligatorio"
                nombre.trim().length < 2 -> "Debe tener al menos 2 caracteres"
                else -> null
            },
            telefono = if (telefono.isNotBlank() && digitos.length !in 8..15)
                "Ingresa un teléfono válido (8 a 15 dígitos)" else null
        )
        if (errores.hayErrores) {
            local.update { it.copy(erroresPerfil = errores) }
            return
        }
        viewModelScope.launch {
            repository.guardarPerfil(
                Perfil(
                    userId = userId,
                    nombre = nombre.trim(),
                    telefono = telefono.trim(),
                    cumpleanos = cumpleanos,
                    ciudad = ciudad.trim(),
                    fotoPath = fotoPath
                )
            )
            local.update { it.copy(erroresPerfil = PerfilErrores()) }
            onGuardado()
        }
    }

    // ---------- Recordatorios ----------

    fun limpiarErroresRecordatorio() = local.update { it.copy(erroresRecordatorio = RecordatorioErrores()) }

    fun agregarRecordatorio(
        mascotaId: Int,
        fecha: Long?,
        motivo: String,
        descripcion: String,
        onGuardado: () -> Unit
    ) {
        val errores = RecordatorioErrores(
            fecha = if (fecha == null) "Selecciona la fecha de la cita" else null,
            motivo = if (motivo.isBlank()) "El motivo es obligatorio" else null,
            descripcion = if (descripcion.isBlank()) "La descripción es obligatoria" else null
        )
        if (errores.hayErrores || fecha == null) {
            local.update { it.copy(erroresRecordatorio = errores) }
            return
        }
        viewModelScope.launch {
            repository.insertarRecordatorio(
                Recordatorio(
                    userId = userId,
                    mascotaId = mascotaId,
                    fecha = fecha,
                    motivo = motivo.trim(),
                    descripcion = descripcion.trim()
                )
            )
            local.update { it.copy(erroresRecordatorio = RecordatorioErrores()) }
            onGuardado()
        }
    }

    fun eliminarRecordatorio(recordatorio: Recordatorio) {
        viewModelScope.launch { repository.eliminarRecordatorio(recordatorio) }
    }
}
