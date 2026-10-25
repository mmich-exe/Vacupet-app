package com.example.vacupet.repository

import com.example.vacupet.data.Mascota
import com.example.vacupet.data.MascotaDao
import com.example.vacupet.data.Recordatorio
import com.example.vacupet.data.RecordatorioDao
import kotlinx.coroutines.flow.Flow

/**
 * Capa intermedia entre los ViewModel y Room.
 * La UI nunca accede directamente a los DAO.
 */
class AppRepository(
    private val mascotaDao: MascotaDao,
    private val recordatorioDao: RecordatorioDao
) {
    // ----- Mascotas (CRUD) -----
    fun observarMascotas(userId: String): Flow<List<Mascota>> = mascotaDao.observarTodas(userId)

    suspend fun obtenerMascota(id: Int, userId: String): Mascota? = mascotaDao.obtenerPorId(id, userId)

    suspend fun insertarMascota(mascota: Mascota) {
        mascotaDao.insertar(mascota)
    }

    suspend fun actualizarMascota(mascota: Mascota) {
        mascotaDao.actualizar(mascota)
    }

    suspend fun eliminarMascota(mascota: Mascota) {
        mascotaDao.eliminar(mascota)
    }

    // ----- Recordatorios -----
    fun observarRecordatorios(userId: String): Flow<List<Recordatorio>> =
        recordatorioDao.observarTodos(userId)

    suspend fun insertarRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.insertar(recordatorio)
    }

    suspend fun eliminarRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.eliminar(recordatorio)
    }
}
