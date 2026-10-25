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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vacupet.R
import com.example.vacupet.components.FondoDegradado
import com.example.vacupet.components.VerdeOscuro
import com.example.vacupet.components.VerdeVacu
import com.example.vacupet.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (email: String, password: String) -> Unit,
    onIrARegistro: () -> Unit,
    onRecuperarPassword: (email: String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    FondoDegradado {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_paw),
                contentDescription = null,
                tint = VerdeVacu,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "VacuPet",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )
            Text(
                text = "Inicia sesión para cuidar a tus mejores amigos",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF388E3C),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

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
                label = { Text("Contraseña") },
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

            if (state.mensaje != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.mensaje,
                    color = VerdeOscuro,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onLogin(email, password) },
                enabled = !state.cargando,
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVacu),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (state.cargando) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = { onRecuperarPassword(email) }, enabled = !state.cargando) {
                Text("¿Olvidaste tu contraseña?", color = VerdeOscuro)
            }
            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? Regístrate", color = VerdeOscuro)
            }
        }
    }
}
