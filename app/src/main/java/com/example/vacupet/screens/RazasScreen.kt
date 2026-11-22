package com.example.vacupet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.vacupet.viewmodel.DogUiState

@Composable
fun RazasScreen(
    state: DogUiState,
    onBusquedaChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = { VacuTopBar(titulo = "Enciclopedia de Razas", onVolver = onVolver) }
    ) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onBusquedaChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("Buscar raza, origen o temperamento...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = VerdeVacu) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdeVacu,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                when {
                    state.cargando -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = VerdeVacu)
                        }
                    }
                    state.showApiKeyInput || state.error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "¡TheDogAPI requiere una API Key!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = VerdeOscuro
                                    )
                                    Text(
                                        text = "Obtén tu API Key gratuita en thedogapi.com (toma 10 segundos) y pégala aquí para ver las razas:",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray
                                    )
                                    OutlinedTextField(
                                        value = state.apiKey,
                                        onValueChange = onApiKeyChange,
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Ej: live_abc123...") },
                                        singleLine = true
                                    )
                                    Button(
                                        onClick = onReintentar,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Cargar con API Key")
                                    }
                                }
                            }
                        }
                    }
                    state.filteredBreeds.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron razas",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.filteredBreeds, key = { it.id }) { breed ->
                                BreedCard(breed = breed, onClick = { onAbrirDetalle(breed.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreedCard(
    breed: Breed,
    onClick: () -> Unit
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = breed.image?.url,
                contentDescription = breed.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = breed.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = VerdeOscuro
                )
                if (!breed.origin.isNullOrBlank()) {
                    Text(
                        text = "Origen: ${TraductorRaza.traducir(breed.origin)}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
                if (!breed.temperament.isNullOrBlank()) {
                    Text(
                        text = TraductorRaza.traducir(breed.temperament),
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }
            Text("›", fontSize = 24.sp, color = VerdeVacu, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
