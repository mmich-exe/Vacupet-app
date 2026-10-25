package com.example.vacupet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vacupet.R
import com.example.vacupet.data.Mascota
import com.example.vacupet.data.Recordatorio

val DoradoFavorito = Color(0xFFFFB300)

@Composable
fun MascotaCard(
    mascota: Mascota,
    proximaCita: Recordatorio?,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FotoMascota(ruta = mascota.fotoPath, tamano = 56.dp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = mascota.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (mascota.cumpleanos.isNotBlank()) "${mascota.especie} • ${mascota.cumpleanos}"
                    else mascota.especie,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                if (proximaCita != null) {
                    Text(
                        text = "🔔 ${formatearFecha(proximaCita.fecha)} · ${proximaCita.motivo}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = NaranjaCita,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Estrella: dorada si es favorita, gris si no.
            IconButton(onClick = onFavorito) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = if (mascota.favorito) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (mascota.favorito) DoradoFavorito else Color(0xFFBDBDBD),
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onEditar) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Editar",
                    tint = VerdeVacu,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
