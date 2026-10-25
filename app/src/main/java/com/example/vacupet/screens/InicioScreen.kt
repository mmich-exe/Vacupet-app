package com.example.vacupet.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vacupet.R
import com.example.vacupet.components.DoradoFavorito
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.FotoMascota
import com.example.vacupet.components.NaranjaCita
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.viewmodel.AppUiState

/** Menú principal que se ve después del login: perfil + accesos a las secciones. */
@Composable
fun InicioScreen(
    email: String,
    state: AppUiState,
    onMascotas: () -> Unit,
    onRecordatorios: () -> Unit,
    onFavoritos: () -> Unit,
    onPerfil: () -> Unit
) {
    Scaffold(topBar = { VacuTopBar(titulo = "VacuPet") }) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tarjeta de perfil (toca para ver el perfil completo)
                Card(
                    onClick = onPerfil,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FotoMascota(ruta = state.perfil?.fotoPath, tamano = 64.dp)
                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Text(
                                text = state.perfil?.nombre?.let { "¡Hola, ${it.substringBefore(' ')}!" }
                                    ?: "¡Bienvenido!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = VerdeOscuro
                            )
                            Text(
                                text = if (state.perfil == null) "Toca para completar tu perfil" else email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text("Ver perfil ›", color = VerdeVacu, fontWeight = FontWeight.Medium)
                    }
                }

                Text(
                    text = "¿Qué quieres hacer?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )

                MenuCard(
                    titulo = "Mis hijos animales",
                    subtitulo = "${state.mascotas.size} registrada(s)",
                    onClick = onMascotas
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pet),
                        contentDescription = null,
                        tint = VerdeVacu,
                        modifier = Modifier.size(30.dp)
                    )
                }
                MenuCard(
                    titulo = "Recordatorios",
                    subtitulo = "${state.citasProximas.size} cita(s) próxima(s)",
                    onClick = onRecordatorios
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_alarm),
                        contentDescription = null,
                        tint = NaranjaCita,
                        modifier = Modifier.size(30.dp)
                    )
                }
                MenuCard(
                    titulo = "Favoritos",
                    subtitulo = "${state.favoritas.size} favorita(s)",
                    onClick = onFavoritos
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = DoradoFavorito,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuCard(
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    icono: @Composable () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) { icono() }
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )
                Text(text = subtitulo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = VerdeVacu)
        }
    }
}
