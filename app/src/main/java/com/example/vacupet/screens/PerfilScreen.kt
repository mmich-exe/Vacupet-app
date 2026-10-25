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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.FotoMascota
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.data.Perfil

/** Vista del perfil (solo lectura). Para modificar datos se usa el botón "Editar perfil". */
@Composable
fun PerfilScreen(
    email: String,
    perfil: Perfil?,
    totalMascotas: Int,
    citasProximas: Int,
    totalFavoritas: Int,
    onEditar: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    Scaffold(topBar = { VacuTopBar(titulo = "Mi perfil", onVolver = onVolver) }) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tarjeta principal del perfil
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FotoMascota(ruta = perfil?.fotoPath, tamano = 110.dp)
                        Text(
                            text = perfil?.nombre ?: "Perfil sin completar",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = VerdeOscuro,
                            textAlign = TextAlign.Center
                        )
                        Text(email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                        if (perfil == null) {
                            Text(
                                text = "Agrega tu nombre, foto y datos de contacto.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DatoPerfil("Teléfono", perfil.telefono.ifBlank { "No registrado" })
                                DatoPerfil("Fecha de nacimiento", perfil.cumpleanos.ifBlank { "No registrada" })
                                DatoPerfil("Ciudad", perfil.ciudad.ifBlank { "No registrada" })
                            }
                        }
                    }
                }

                // Resumen de actividad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tu actividad", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text("🐾 $totalMascotas mascota(s) registradas")
                        Text("⭐ $totalFavoritas favorita(s)")
                        Text("🔔 $citasProximas cita(s) próxima(s)")
                    }
                }

                Button(
                    onClick = onEditar,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (perfil == null) "Completar perfil" else "Editar perfil") }

                OutlinedButton(
                    onClick = { confirmarSalida = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Text("  Cerrar sesión")
                }
            }
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres salir de tu cuenta? Tus datos quedan guardados.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    onCerrarSesion()
                }) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = etiqueta, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}
