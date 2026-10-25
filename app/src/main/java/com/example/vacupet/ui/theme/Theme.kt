package com.example.vacupet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaVacuPet = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFE65100),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFFBF360C),
    background = Color(0xFFF1FAF4),
    surface = Color(0xFFF1FAF4)
)

/** Tema claro de VacuPet (verde). */
@Composable
fun VacuPetTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaVacuPet, content = content)
}
