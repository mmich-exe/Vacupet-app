package com.example.vacupet.components

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.vacupet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val VerdeVacu = Color(0xFF2E7D32)
val VerdeOscuro = Color(0xFF1B5E20)
val NaranjaCita = Color(0xFFE65100)

// ---------------------------------------------------------------- fechas

fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date(milisegundos))

/** Abre el selector de fecha de Android y devuelve la fecha elegida en milisegundos. */
fun mostrarSelectorFecha(context: Context, onFechaElegida: (Long) -> Unit) {
    val hoy = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            val elegida = Calendar.getInstance().apply {
                clear()
                set(anio, mes, dia)
            }
            onFechaElegida(elegida.timeInMillis)
        },
        hoy.get(Calendar.YEAR),
        hoy.get(Calendar.MONTH),
        hoy.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// ---------------------------------------------------------------- fotos

/**
 * Copia la imagen elegida en la galería al almacenamiento interno de la app.
 * Así la foto sigue disponible aunque se cierre la app o se pierda el permiso de la galería.
 */
fun copiarFotoAInterno(context: Context, uri: Uri): String? = try {
    val destino = File(context.filesDir, "mascota_${System.currentTimeMillis()}.img")
    context.contentResolver.openInputStream(uri)?.use { entrada ->
        destino.outputStream().use { salida -> entrada.copyTo(salida) }
    }
    destino.absolutePath
} catch (_: Exception) {
    null
}

private fun decodificar(ruta: String, maxLado: Int): Bitmap? {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(ruta, limites)
    var muestra = 1
    while (limites.outWidth / muestra > maxLado * 2 || limites.outHeight / muestra > maxLado * 2) {
        muestra *= 2
    }
    return BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = muestra })
}

/** Foto circular de la mascota; si no tiene foto muestra el ícono de mascota. */
@Composable
fun FotoMascota(ruta: String?, tamano: Dp, modifier: Modifier = Modifier) {
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = ruta) {
        value = if (ruta == null) null else withContext(Dispatchers.IO) { decodificar(ruta, 300) }
    }
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(Color(0xFFE8F5E9)),
        contentAlignment = Alignment.Center
    ) {
        val imagen = bitmap
        if (imagen != null) {
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = "Foto de la mascota",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_pet),
                contentDescription = "Mascota",
                tint = VerdeVacu,
                modifier = Modifier.size(tamano * 0.6f)
            )
        }
    }
}

// ---------------------------------------------------------------- estilo

@Composable
fun FondoDegradado(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(Color(0xFFDCF2F6), Color(0xFFEAF7EE)))
            )
    ) { content() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacuTopBar(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VerdeVacu,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
