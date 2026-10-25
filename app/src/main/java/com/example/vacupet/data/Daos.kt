package com.example.vacupet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    // CREATE
    @Insert
    suspend fun insertar(mascota: Mascota): Long

    // READ (Flow: la UI se actualiza sola cuando cambia la tabla)
    @Query("SELECT * FROM mascotas WHERE userId = :userId ORDER BY nombre COLLATE NOCASE")
    fun observarTodas(userId: String): Flow<List<Mascota>>

    @Query("SELECT * FROM mascotas WHERE id = :id AND userId = :userId")
    suspend fun obtenerPorId(id: Int, userId: String): Mascota?

    // UPDATE
    @Update
    suspend fun actualizar(mascota: Mascota)

    // DELETE
    @Delete
    suspend fun eliminar(mascota: Mascota)
}

@Dao
interface RecordatorioDao {

    @Insert
    suspend fun insertar(recordatorio: Recordatorio): Long

    @Query("SELECT * FROM recordatorios WHERE userId = :userId ORDER BY fecha ASC")
    fun observarTodos(userId: String): Flow<List<Recordatorio>>

    @Delete
    suspend fun eliminar(recordatorio: Recordatorio)
}
