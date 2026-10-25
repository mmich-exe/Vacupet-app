package com.example.vacupet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Mascota::class, Recordatorio::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mascotaDao(): MascotaDao
    abstract fun recordatorioDao(): RecordatorioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vacupet.db"
                ).build().also { instancia = it }
            }
    }
}
