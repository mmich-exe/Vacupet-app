package com.example.vacupet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Mascota::class, Recordatorio::class, Perfil::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mascotaDao(): MascotaDao
    abstract fun recordatorioDao(): RecordatorioDao
    abstract fun perfilDao(): PerfilDao

    companion object {
        /** Versión 2: se agrega la tabla de perfiles sin perder mascotas ni recordatorios. */
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `perfiles` (`userId` TEXT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, `telefono` TEXT NOT NULL, `cumpleanos` TEXT NOT NULL, " +
                        "`ciudad` TEXT NOT NULL, `fotoPath` TEXT, PRIMARY KEY(`userId`))"
                )
            }
        }

        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vacupet.db"
                ).addMigrations(MIGRACION_1_2).build().also { instancia = it }
            }
    }
}
