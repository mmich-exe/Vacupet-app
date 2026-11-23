package com.example.vacupet.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.vacupet.components.copiarFotoAInterno
import com.example.vacupet.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearAvisoPerdidoScreen(
    viewModel: CommunityViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Categoría: Perro, Gato, Otro
    var selectedCategory by remember { mutableStateOf("Perro") }
    var selectedBreed by remember { mutableStateOf("Mestizo / Desconocido") }
    var otherPetType by remember { mutableStateOf("") }

    // Dropdown expansion state
    var expanded by remember { mutableStateOf(false) }

    val dogBreeds by viewModel.dogBreeds.collectAsState()
    val catBreeds by viewModel.catBreeds.collectAsState()

    val currentBreeds = when (selectedCategory) {
        "Perro" -> dogBreeds
        "Gato" -> catBreeds
        else -> emptyList()
    }

    val context = LocalContext.current
    val tempImageUri by viewModel.tempImageUri.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var photoUriString by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            photoUriString = tempImageUri.toString()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val internalPath = copiarFotoAInterno(context, uri)
            if (internalPath != null) {
                photoUriString = internalPath
                Toast.makeText(context, "Foto cargada desde la galería", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.createTempImageUri(context)
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.fetchCurrentLocation(context)
            Toast.makeText(context, "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Mascota Perdida") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título (Ej: Perro perdido en Parque)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de Categoría (Perro, Gato, Otro)
            Text("Tipo de Mascota:", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Perro", "Gato", "Otro").forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = category
                            if (category == "Perro" && dogBreeds.isNotEmpty()) {
                                selectedBreed = dogBreeds.first()
                            } else if (category == "Gato" && catBreeds.isNotEmpty()) {
                                selectedBreed = catBreeds.first()
                            }
                        },
                        label = { Text(category) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Selección de Raza (si es Perro o Gato) o Texto libre (si es Otro)
            if (selectedCategory == "Otro") {
                OutlinedTextField(
                    value = otherPetType,
                    onValueChange = { otherPetType = it },
                    label = { Text("Especie / Tipo (Ej: Conejo, Ave, etc.)") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Exposed Dropdown Menu para Razas de la API
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedBreed,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Raza (API de ${selectedCategory}s)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        currentBreeds.forEach { breed ->
                            DropdownMenuItem(
                                text = { Text(breed) },
                                onClick = {
                                    selectedBreed = breed
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción y detalles de contacto") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Camera & Gallery Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (photoUriString.isNotBlank()) {
                        AsyncImage(
                            model = photoUriString,
                            contentDescription = "Foto de mascota",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val permissionCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    val uri = viewModel.createTempImageUri(context)
                                    cameraLauncher.launch(uri)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Tomar foto")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cámara")
                        }

                        Button(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Galería")
                        }
                    }
                }
            }

            // GPS Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ubicación GPS: Lat ${String.format("%.4f", currentLocation.first)}, Lng ${String.format("%.4f", currentLocation.second)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val fineLocationCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            if (fineLocationCheck == PackageManager.PERMISSION_GRANTED) {
                                viewModel.fetchCurrentLocation(context)
                                Toast.makeText(context, "Ubicación actualizada", Toast.LENGTH_SHORT).show()
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Obtener ubicación")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Obtener Ubicación GPS Actual")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank()) {
                        Toast.makeText(context, "Por favor completa el título y la descripción", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val finalBreedName = if (selectedCategory == "Otro") {
                        if (otherPetType.isBlank()) "Otro animal / Desconocido" else otherPetType
                    } else {
                        "$selectedCategory ($selectedBreed)"
                    }

                    viewModel.addPost(
                        title = title,
                        description = description,
                        breedName = finalBreedName,
                        localPhotoUri = photoUriString,
                        latitude = currentLocation.first,
                        longitude = currentLocation.second
                    ) { success, errorMsg ->
                        if (success) {
                            Toast.makeText(context, "Aviso publicado con éxito", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(context, errorMsg ?: "Error al publicar aviso", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Publicar Aviso")
                }
            }
        }
    }
}
