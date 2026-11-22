package com.example.vacupet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.data.model.Breed
import com.example.vacupet.utils.TraductorRaza

@Composable
fun DetalleRazaScreen(
    breed: Breed?,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = { VacuTopBar(titulo = breed?.name ?: "Detalle de Raza", onVolver = onVolver) }
    ) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            if (breed == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Raza no encontrada", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imagen principal
                    AsyncImage(
                        model = breed.image?.url,
                        contentDescription = breed.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                    )

                    // Tarjeta de información principal
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = breed.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeOscuro
                            )

                            InfoRow(label = "Origen", value = TraductorRaza.traducir(breed.origin))
                            InfoRow(label = "Grupo", value = TraductorRaza.traducir(breed.breedGroup))
                            InfoRow(label = "Función / Uso", value = TraductorRaza.traducir(breed.bredFor))
                            InfoRow(label = "Esperanza de vida", value = breed.lifeSpan ?: "No especificada")
                            InfoRow(label = "Peso", value = "${breed.weight?.metric ?: "N/A"} kg")
                            InfoRow(label = "Altura", value = "${breed.height?.metric ?: "N/A"} cm")
                        }
                    }

                    // Tarjeta de temperamento
                    if (!breed.temperament.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Temperamento",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerdeVacu
                                )
                                Text(
                                    text = TraductorRaza.traducir(breed.temperament),
                                    fontSize = 15.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, color = VerdeOscuro)
    }
}
