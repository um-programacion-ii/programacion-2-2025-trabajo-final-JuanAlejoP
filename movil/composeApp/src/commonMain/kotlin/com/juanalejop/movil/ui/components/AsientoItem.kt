package com.juanalejop.movil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juanalejop.movil.data.model.Asiento

@Composable
fun AsientoItem(
    asiento: Asiento,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Definimos el color según el estado
    val backgroundColor = when {
        asiento.estado == "Vendido" -> Color.Red.copy(alpha = 0.5f)
        asiento.estado == "Bloqueado" -> Color.Gray.copy(alpha = 0.5f)
        isSelected -> MaterialTheme.colorScheme.primary // Seleccionado por mí
        else -> Color.Green.copy(alpha = 0.3f) // Libre
    }

    val isClickable = asiento.estado == "Libre" // Solo se pueden tocar los libres

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable(enabled = isClickable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Mostramos Fila-Columna chiquito (opcional, ayuda a debuggear)
        Text(
            text = "${asiento.fila}-${asiento.columna}",
            fontSize = 10.sp,
            color = Color.Black
        )
    }
}