package com.example.vacupet.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Especies disponibles en el formulario y en los filtros. */
val ESPECIES = listOf("Perro", "Gato", "Ave", "Conejo", "Otro")

/**
 * Entidad principal (CRUD completo): una mascota.
 * [userId] es el uid de Firebase: cada usuario ve solo sus mascotas.
 * [fotoPath] es la ruta de la foto copiada al almacenamiento interno de la app.
 */
@Entity(tableName = "mascotas", indices = [Index("userId")])
data class Mascota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val nombre: String,
    val especie: String,
    val fotoPath: String? = null,
    val cumpleanos: String = "",
    val vacunas: String = "",
    val infoExtra: String = ""
)

/**
 * Recordatorio de cita (veterinario, vacuna, control...) asociado a una mascota.
 * Si se elimina la mascota, sus recordatorios se eliminan en cascada.
 */
@Entity(
    tableName = "recordatorios",
    foreignKeys = [
        ForeignKey(
            entity = Mascota::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mascotaId"), Index("userId")]
)
data class Recordatorio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val mascotaId: Int,
    val fecha: Long,
    val motivo: String,
    val descripcion: String
)

/**
 * Perfil del usuario (no de sus mascotas). La clave es el uid de Firebase,
 * por lo que cada cuenta tiene como máximo un perfil.
 */
@Entity(tableName = "perfiles")
data class Perfil(
    @PrimaryKey val userId: String,
    val nombre: String,
    val telefono: String = "",
    val cumpleanos: String = "",
    val ciudad: String = "",
    val fotoPath: String? = null
)
