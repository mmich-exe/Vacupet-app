package com.example.vacupet.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vacupet.R
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.MascotaCard
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.data.ESPECIES
import com.example.vacupet.viewmodel.AppUiState

/** Pantalla principal: lista de mascotas con búsqueda y filtro por especie. */
@Composable
fun ListadoScreen(
    state: AppUiState,
    onBusquedaChange: (String) -> Unit,
    onFiltroEspecie: (String?) -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    onEditar: (Int) -> Unit,
    onNueva: () -> Unit,
    onAgenda: () -> Unit,
    onPerfil: () -> Unit
) {
    val lista = state.mascotasFiltradas

    Scaffold(
        topBar = {
            VacuTopBar(
                titulo = "Mis hijos animales",
                acciones = {
                    IconButton(onClick = onAgenda) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alarm),
                            contentDescription = "Agenda de citas",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onPerfil) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNueva,
                containerColor = VerdeVacu,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Registrar mascota",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!state.cargando) {
                    val saludo = state.perfil?.nombre?.substringBefore(' ')
                    Text(
                        text = if (saludo != null) "¡Hola, $saludo!" else "Completa tu perfil para personalizar la app",
                        fontWeight = FontWeight.SemiBold,
                        color = VerdeOscuro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPerfil() }
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    )
                }
                if (state.mascotas.isNotEmpty()) {
                    OutlinedTextField(
                        value = state.busqueda,
                        onValueChange = onBusquedaChange,
                        label = { Text("Buscar por nombre") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.busqueda.isNotEmpty()) {
                                IconButton(onClick = { onBusquedaChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.filtroEspecie == null,
                            onClick = { onFiltroEspecie(null) },
                            label = { Text("Todas") }
                        )
                        ESPECIES.forEach { especie ->
                            FilterChip(
                                selected = state.filtroEspecie == especie,
                                onClick = { onFiltroEspecie(especie) },
                                label = { Text(especie) }
                            )
                        }
                    }
                }

                if (lista.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pet),
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = if (state.mascotas.isEmpty())
                                "No hay mascotas registradas, ¡registra tu mascota!"
                            else "Ninguna mascota coincide con la búsqueda o el filtro.",
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
                        items(lista, key = { it.id }) { mascota ->
                            MascotaCard(
                                mascota = mascota,
                                proximaCita = state.proximaCita(mascota.id),
                                onClick = { onAbrirDetalle(mascota.id) },
                                onEditar = { onEditar(mascota.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
