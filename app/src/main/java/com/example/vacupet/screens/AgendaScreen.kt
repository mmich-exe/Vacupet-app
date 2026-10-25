package com.example.vacupet.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.NaranjaCita
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.formatearFecha
import com.example.vacupet.data.Recordatorio
import com.example.vacupet.viewmodel.AppUiState

/** Funcionalidad propia: agenda con las citas de TODAS las mascotas, próximas y pasadas. */
@Composable
fun AgendaScreen(
    state: AppUiState,
    onAbrirMascota: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val proximas = state.citasProximas
    val pasadas = state.citasPasadas

    Scaffold(topBar = { VacuTopBar(titulo = "Agenda de citas", onVolver = onVolver) }) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "Próximas citas (${proximas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuro
                    )
                }
                if (proximas.isEmpty()) {
                    item { Text("No tienes citas próximas.", color = Color.Gray) }
                } else {
                    items(proximas, key = { "p${it.id}" }) { cita ->
                        CitaCard(cita, state.mascotaPorId(cita.mascotaId)?.nombre ?: "Mascota", false, onAbrirMascota)
                    }
                }
                if (pasadas.isNotEmpty()) {
                    item {
                        Text(
                            "Citas pasadas (${pasadas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    items(pasadas, key = { "h${it.id}" }) { cita ->
                        CitaCard(cita, state.mascotaPorId(cita.mascotaId)?.nombre ?: "Mascota", true, onAbrirMascota)
                    }
                }
            }
        }
    }
}

@Composable
private fun CitaCard(
    cita: Recordatorio,
    nombreMascota: String,
    pasada: Boolean,
    onAbrirMascota: (Int) -> Unit
) {
    Card(
        onClick = { onAbrirMascota(cita.mascotaId) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (pasada) Color(0xFFEEEEEE) else Color(0xFFFFF3E0)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "${formatearFecha(cita.fecha)} · $nombreMascota",
                fontWeight = FontWeight.Bold,
                color = if (pasada) Color.Gray else NaranjaCita
            )
            Text(cita.motivo, style = MaterialTheme.typography.bodyLarge)
            if (cita.descripcion.isNotBlank()) {
                Text(cita.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}
