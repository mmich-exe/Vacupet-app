package com.example.vacupet.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vacupet.components.DoradoFavorito
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.MascotaCard
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.data.Mascota
import com.example.vacupet.viewmodel.AppUiState

/** Muestra solo las mascotas marcadas con la estrella. */
@Composable
fun FavoritosScreen(
    state: AppUiState,
    onAbrirDetalle: (Int) -> Unit,
    onEditar: (Int) -> Unit,
    onFavorito: (Mascota) -> Unit,
    onVolver: () -> Unit
) {
    val favoritas = state.favoritas

    Scaffold(topBar = { VacuTopBar(titulo = "Favoritos", onVolver = onVolver) }) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            if (favoritas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = DoradoFavorito,
                        modifier = Modifier
                            .size(90.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Aún no tienes favoritos.\nToca la estrella de una mascota para agregarla aquí.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF558B2F),
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoritas, key = { it.id }) { mascota ->
                        MascotaCard(
                            mascota = mascota,
                            proximaCita = state.proximaCita(mascota.id),
                            onClick = { onAbrirDetalle(mascota.id) },
                            onEditar = { onEditar(mascota.id) },
                            onFavorito = { onFavorito(mascota) }
                        )
                    }
                }
            }
        }
    }
}
