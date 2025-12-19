package com.juanalejop.movil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juanalejop.movil.data.model.Asiento
// Importa tus colores si es necesario, o usa los hardcoded abajo para mantener consistencia con lo hablado

@Composable
fun AsientoItem(
    asiento: Asiento,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val isClickable = asiento.estado == "Libre"

    // --- LÓGICA DE COLOR DE FONDO (Igual que antes) ---
    val backgroundColor = if (isDark) {
        when {
            asiento.estado == "Vendido" -> Color(0xFFB71C1C) // Rojo oscuro
            asiento.estado == "Bloqueado" -> Color.DarkGray
            isSelected -> MaterialTheme.colorScheme.primary // Electric Teal
            else -> Color(0xFF1B5E20) // Verde oscuro
        }
    } else {
        // Tu lógica original para modo claro
        when {
            asiento.estado == "Vendido" -> Color.Red.copy(alpha = 0.5f)
            asiento.estado == "Bloqueado" -> Color.Gray.copy(alpha = 0.5f)
            isSelected -> MaterialTheme.colorScheme.primary // Dark Teal
            else -> Color.Green.copy(alpha = 0.3f)
        }
    }

    // --- LÓGICA DE COLOR DE TEXTO (MEJORADA) 🎨 ---
    val contentColor = if (isDark) {
        // En Dark Mode:
        // Si está seleccionado (fondo Teal brillante) -> Texto NEGRO
        // Si no (fondo Rojo/Verde oscuro) -> Texto BLANCO
        if (isSelected) Color.Black else Color.White
    } else {
        // En Light Mode: Siempre negro (o gris oscuro)
        Color.Black
    }

    val contentAlpha = if (isDark && !isSelected) 0.9f else 0.8f
    val borderColor = if (isDark) Color.Gray else Color.Gray

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = isClickable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "F${asiento.fila}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor.copy(alpha = contentAlpha),
                lineHeight = 10.sp
            )
            Text(
                text = "C${asiento.columna}",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                lineHeight = 11.sp
            )
        }
    }
}