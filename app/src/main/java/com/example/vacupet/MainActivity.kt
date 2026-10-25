package com.example.vacupet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.vacupet.data.AppDatabase
import com.example.vacupet.navigation.AppNavigation
import com.example.vacupet.repository.AppRepository
import com.example.vacupet.ui.theme.VacuPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = AppRepository(database.mascotaDao(), database.recordatorioDao())

        setContent {
            VacuPetTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(repository = repository)
                }
            }
        }
    }
}
