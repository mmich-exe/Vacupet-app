package com.example.vacupet.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.NaranjaCita
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.components.formatearFecha
import com.example.vacupet.components.mostrarSelectorFecha
import com.example.vacupet.data.Mascota
import com.example.vacupet.data.Recordatorio
import com.example.vacupet.viewmodel.RecordatorioErrores
import java.util.Calendar

/** Agregar y eliminar recordatorios (citas, vacunas, controles) de una mascota. */
@Composable
fun RecordatoriosScreen(
    mascota: Mascota?,
    citas: List<Recordatorio>,
    errores: RecordatorioErrores,
    onAgregar: (fecha: Long?, motivo: String, descripcion: String, onGuardado: () -> Unit) -> Unit,
    onEliminar: (Recordatorio) -> Unit,
    onLimpiarErrores: () -> Unit,
    onVolver: () -> Unit
) {
    LaunchedEffect(Unit) { onLimpiarErrores() }

    var fecha by rememberSaveable { mutableStateOf<Long?>(null) }
    var motivo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            VacuTopBar(
                titulo = "Recordatorios de ${mascota?.nombre ?: ""}".trim(),
                onVolver = onVolver
            )
        }
    ) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Nueva cita", fontWeight = FontWeight.Bold, color = NaranjaCita)

                            val inicioDeManana = remember {
                                Calendar.getInstance().apply {
                                    add(Calendar.DAY_OF_MONTH, 1)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }.timeInMillis
                            }
                            val abrirSelectorFecha = {
                                mostrarSelectorFecha(context, minFecha = inicioDeManana) { fecha = it }
                            }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = fecha?.let { formatearFecha(it) } ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Fecha de la cita *") },
                                    isError = errores.fecha != null,
                                    supportingText = { errores.fecha?.let { Text(it) } },
                                    trailingIcon = {
                                        IconButton(onClick = abrirSelectorFecha) {
                                            Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable(onClick = abrirSelectorFecha)
                                )
                            }
                            OutlinedTextField(
                                value = motivo,
                                onValueChange = { motivo = it },
                                label = { Text("Motivo de la cita *") },
                                singleLine = true,
                                isError = errores.motivo != null,
                                supportingText = { errores.motivo?.let { Text(it) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción *") },
                                minLines = 2,
                                isError = errores.descripcion != null,
                                supportingText = { errores.descripcion?.let { Text(it) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    if (mascota != null) {
                                        onAgregar(fecha, motivo, descripcion) {
                                            fecha = null
                                            motivo = ""
                                            descripcion = ""
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Agregar cita") }
                        }
                    }
                }

                item {
                    Text(
                        text = "Citas registradas (${citas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (citas.isEmpty()) {
                    item { Text("Aún no hay citas para esta mascota.", color = Color.Gray) }
                } else {
                    items(citas, key = { it.id }) { cita ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${formatearFecha(cita.fecha)} · ${cita.motivo}",
                                        fontWeight = FontWeight.Bold,
                                        color = NaranjaCita
                                    )
                                    Text(cita.descripcion, style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = { onEliminar(cita) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar cita")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
