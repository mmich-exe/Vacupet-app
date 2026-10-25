package com.example.vacupet.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.FotoMascota
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.components.copiarFotoAInterno
import com.example.vacupet.components.formatearFecha
import com.example.vacupet.components.mostrarSelectorFecha
import com.example.vacupet.data.ESPECIES
import com.example.vacupet.data.Mascota
import com.example.vacupet.viewmodel.MascotaErrores
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Sirve para registrar (mascota == null) y para editar (mascota != null). */
@Composable
fun FormularioScreen(
    mascota: Mascota?,
    errores: MascotaErrores,
    onGuardar: (
        nombre: String,
        especie: String,
        fotoPath: String?,
        cumpleanos: String,
        vacunas: String,
        infoExtra: String
    ) -> Unit,
    onLimpiarErrores: () -> Unit,
    onVolver: () -> Unit
) {
    // Al abrir el formulario se descartan errores de una visita anterior.
    LaunchedEffect(Unit) { onLimpiarErrores() }

    var nombre by rememberSaveable(mascota?.id) { mutableStateOf(mascota?.nombre ?: "") }
    var especie by rememberSaveable(mascota?.id) { mutableStateOf(mascota?.especie ?: "") }
    var fotoPath by rememberSaveable(mascota?.id) { mutableStateOf(mascota?.fotoPath) }
    var cumpleanos by rememberSaveable(mascota?.id) { mutableStateOf(mascota?.cumpleanos ?: "") }
    var vacunas by rememberSaveable(mascota?.id) { mutableStateOf(mascota?.vacunas ?: "") }
    var infoExtra by rememberSaveable(mascota?.id) { mutableStateOf(mascota?.infoExtra ?: "") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Selector de galería: la imagen se copia al almacenamiento interno de la app.
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val ruta = withContext(Dispatchers.IO) { copiarFotoAInterno(context, uri) }
                if (ruta != null) fotoPath = ruta
            }
        }
    }

    Scaffold(
        topBar = {
            VacuTopBar(
                titulo = if (mascota == null) "Registrar mascota" else "Editar mascota",
                onVolver = onVolver
            )
        }
    ) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FotoMascota(
                    ruta = fotoPath,
                    tamano = 110.dp,
                    modifier = Modifier.clickable { galeria.launch("image/*") }
                )
                Text(
                    text = if (fotoPath == null) "Toca el círculo para elegir una foto" else "Toca para cambiar la foto",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    isError = errores.nombre != null,
                    supportingText = { errores.nombre?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Especie *", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ESPECIES.forEach { opcion ->
                            FilterChip(
                                selected = especie == opcion,
                                onClick = { especie = opcion },
                                label = { Text(opcion) }
                            )
                        }
                    }
                    if (errores.especie != null) {
                        Text(
                            text = errores.especie,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                OutlinedTextField(
                    value = cumpleanos,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cumpleaños (opcional)") },
                    trailingIcon = {
                        IconButton(onClick = {
                            mostrarSelectorFecha(context) { cumpleanos = formatearFecha(it) }
                        }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = vacunas,
                    onValueChange = { vacunas = it },
                    label = { Text("Vacunas (opcional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = infoExtra,
                    onValueChange = { infoExtra = it },
                    label = { Text("Información adicional (opcional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onGuardar(nombre, especie, fotoPath, cumpleanos, vacunas, infoExtra) },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (mascota == null) "Guardar mascota" else "Guardar cambios")
                }
            }
        }
    }
}
