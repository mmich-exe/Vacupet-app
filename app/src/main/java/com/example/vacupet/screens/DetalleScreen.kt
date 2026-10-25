package com.example.vacupet.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.FotoMascota
import com.example.vacupet.components.NaranjaCita
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.components.formatearFecha
import com.example.vacupet.data.Mascota
import com.example.vacupet.data.Recordatorio

@Composable
fun DetalleScreen(
    mascota: Mascota?,
    citas: List<Recordatorio>,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onRecordatorios: () -> Unit,
    onEliminar: () -> Unit
) {
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            VacuTopBar(
                titulo = "Detalle de la mascota",
                onVolver = onVolver,
                acciones = {
                    if (mascota != null) {
                        IconButton(onClick = { confirmarEliminar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar mascota")
                        }
                    }
                }
            )
        }
    ) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            if (mascota == null) {
                Text("Mascota no encontrada.", modifier = Modifier.padding(16.dp))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FotoMascota(ruta = mascota.fotoPath, tamano = 140.dp)
                    Text(
                        text = mascota.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuro
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DatoDetalle("Especie", mascota.especie)
                            DatoDetalle("Cumpleaños", mascota.cumpleanos.ifBlank { "No registrado" })
                            DatoDetalle("Vacunas", mascota.vacunas.ifBlank { "No registradas" })
                            DatoDetalle("Información adicional", mascota.infoExtra.ifBlank { "Sin información" })
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🔔 Citas y recordatorios (${citas.size})",
                                fontWeight = FontWeight.Bold,
                                color = NaranjaCita
                            )
                            if (citas.isEmpty()) {
                                Text("Sin citas registradas.", color = Color.Gray)
                            } else {
                                citas.take(3).forEach { cita ->
                                    Text("${formatearFecha(cita.fecha)} · ${cita.motivo}")
                                }
                                if (citas.size > 3) Text("…y ${citas.size - 3} más", color = Color.Gray)
                            }
                        }
                    }

                    Button(
                        onClick = onRecordatorios,
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Gestionar recordatorios") }
                    OutlinedButton(onClick = onEditar, modifier = Modifier.fillMaxWidth()) {
                        Text("Editar mascota", color = VerdeVacu)
                    }
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar mascota") },
            text = { Text("Se eliminarán también sus recordatorios. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        onEliminar()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DatoDetalle(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}
