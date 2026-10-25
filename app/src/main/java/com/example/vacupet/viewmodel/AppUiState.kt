package com.example.vacupet.viewmodel

import com.example.vacupet.data.Mascota
import com.example.vacupet.data.Perfil
import com.example.vacupet.data.Recordatorio
import java.util.Calendar

/** Errores de validación del formulario de mascota (null = campo válido). */
data class MascotaErrores(
    val nombre: String? = null,
    val especie: String? = null
) {
    val hayErrores: Boolean get() = nombre != null || especie != null
}

/** Errores de validación del formulario de recordatorio. */
data class RecordatorioErrores(
    val fecha: String? = null,
    val motivo: String? = null,
    val descripcion: String? = null
) {
    val hayErrores: Boolean get() = fecha != null || motivo != null || descripcion != null
}

/** Errores de validación del perfil. */
data class PerfilErrores(
    val nombre: String? = null,
    val telefono: String? = null
) {
    val hayErrores: Boolean get() = nombre != null || telefono != null
}

/** Estado completo que la UI observa. */
data class AppUiState(
    val cargando: Boolean = true,
    val mascotas: List<Mascota> = emptyList(),
    val recordatorios: List<Recordatorio> = emptyList(),
    val perfil: Perfil? = null,
    val busqueda: String = "",
    val filtroEspecie: String? = null,
    val erroresMascota: MascotaErrores = MascotaErrores(),
    val erroresRecordatorio: RecordatorioErrores = RecordatorioErrores(),
    val erroresPerfil: PerfilErrores = PerfilErrores()
) {
    /** Mascotas filtradas por búsqueda (nombre) y especie. */
    val mascotasFiltradas: List<Mascota>
        get() = mascotas.filter { m ->
            (busqueda.isBlank() || m.nombre.contains(busqueda, ignoreCase = true)) &&
                (filtroEspecie == null || m.especie == filtroEspecie)
        }

    /** Mascotas marcadas con la estrella. */
    val favoritas: List<Mascota>
        get() = mascotas.filter { it.favorito }

    /** Medianoche de hoy: las citas de hoy cuentan como próximas. */
    private val inicioDeHoy: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun recordatoriosDe(mascotaId: Int): List<Recordatorio> =
        recordatorios.filter { it.mascotaId == mascotaId }

    /** Próxima cita futura de una mascota (la más cercana), o null. */
    fun proximaCita(mascotaId: Int): Recordatorio? =
        recordatoriosDe(mascotaId).firstOrNull { it.fecha >= inicioDeHoy }

    /** Citas futuras de todas las mascotas, ordenadas por fecha (pantalla Agenda). */
    val citasProximas: List<Recordatorio>
        get() = recordatorios.filter { it.fecha >= inicioDeHoy }

    val citasPasadas: List<Recordatorio>
        get() = recordatorios.filter { it.fecha < inicioDeHoy }.sortedByDescending { it.fecha }

    fun mascotaPorId(id: Int): Mascota? = mascotas.find { it.id == id }
}
