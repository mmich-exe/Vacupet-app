package com.example.vacupet.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.FotoMascota
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.components.copiarFotoAInterno
import com.example.vacupet.components.formatearFecha
import com.example.vacupet.components.mostrarSelectorFecha
import com.example.vacupet.data.Perfil
import com.example.vacupet.viewmodel.PerfilErrores
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Datos del usuario (no de sus mascotas): nombre, foto, teléfono, cumpleaños y ciudad. */
@Composable
fun PerfilScreen(
    email: String,
    perfil: Perfil?,
    totalMascotas: Int,
    citasProximas: Int,
    errores: PerfilErrores,
    onGuardar: (
        nombre: String,
        telefono: String,
        cumpleanos: String,
        ciudad: String,
        fotoPath: String?,
        onGuardado: () -> Unit
    ) -> Unit,
    onLimpiarErrores: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    LaunchedEffect(Unit) { onLimpiarErrores() }

    // La clave es "perfil != null": los campos se cargan cuando el perfil llega desde Room.
    var nombre by rememberSaveable(perfil != null) { mutableStateOf(perfil?.nombre ?: "") }
    var telefono by rememberSaveable(perfil != null) { mutableStateOf(perfil?.telefono ?: "") }
    var cumpleanos by rememberSaveable(perfil != null) { mutableStateOf(perfil?.cumpleanos ?: "") }
    var ciudad by rememberSaveable(perfil != null) { mutableStateOf(perfil?.ciudad ?: "") }
    var fotoPath by rememberSaveable(perfil != null) { mutableStateOf(perfil?.fotoPath) }
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val ruta = withContext(Dispatchers.IO) { copiarFotoAInterno(context, uri) }
                if (ruta != null) fotoPath = ruta
            }
        }
    }

    Scaffold(
        topBar = { VacuTopBar(titulo = "Mi perfil", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbar) }
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
                    tamano = 120.dp,
                    modifier = Modifier.clickable { galeria.launch("image/*") }
                )
                Text(
                    text = if (fotoPath == null) "Toca el círculo para elegir tu foto" else "Toca para cambiar tu foto",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tu actividad", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text("🐾 $totalMascotas mascota(s) registradas")
                        Text("🔔 $citasProximas cita(s) próxima(s)")
                    }
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo *") },
                    singleLine = true,
                    isError = errores.nombre != null,
                    supportingText = { errores.nombre?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono celular") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = errores.telefono != null,
                    supportingText = { errores.telefono?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cumpleanos,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de nacimiento") },
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
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        onGuardar(nombre, telefono, cumpleanos, ciudad, fotoPath) {
                            scope.launch { snackbar.showSnackbar("Perfil guardado") }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar perfil") }

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
