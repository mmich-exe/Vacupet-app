package com.example.vacupet.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.VacuTopBar
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.viewmodel.AuthUiState

@Composable
fun RegistroScreen(
    state: AuthUiState,
    onRegistrar: (email: String, password: String, confirmacion: String) -> Unit,
    onVolver: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }

    Scaffold(topBar = { VacuTopBar(titulo = "¡Bienvenido a VacuPet!", onVolver = onVolver) }) { padding ->
        FondoDegradado(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crea tu cuenta para cuidar a tus mejores amigos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF388E3C)
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña (mínimo 6 caracteres)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmacion,
                    onValueChange = { confirmacion = it },
                    label = { Text("Repetir contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { onRegistrar(email, password, confirmacion) },
                    enabled = !state.cargando,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    if (state.cargando) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("Registrarme", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
