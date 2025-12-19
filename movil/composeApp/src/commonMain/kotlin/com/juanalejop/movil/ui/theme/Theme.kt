package com.juanalejop.movil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta Oscura (Dark Mode) 🌙
private val DarkColorScheme = darkColorScheme(
    primary = ElectricTeal,       // Botones brillan neón
    primaryContainer = ElectricTeal, // Contenedores más apagados
    secondary = ElectricTeal,     // Textos secundarios resaltan

    background = Black,           // Fondo total negro
    surface = DarkSurface,        // Tarjetas gris oscuro (NO negro puro)
    surfaceVariant = DarkSurface, // Inputs también gris oscuro

    onPrimary = Black,            // Texto sobre botón neón -> Negro
    onSecondary = Black,
    onBackground = White,         // Texto sobre fondo negro -> Blanco
    onSurface = White,            // Texto sobre tarjetas -> Blanco
    onSurfaceVariant = ElectricTeal, // Iconos/Labels -> Neón

    outline = ElectricTeal,        // Bordes de inputs -> Neón
    error= Color(0xFFCF6679)
)

// Paleta Clara (Light Mode) ☀️
private val LightColorScheme = lightColorScheme(
    primary = DarkTeal,
    primaryContainer = ElectricTeal,
    secondary = DeepOcean,

    background = White,
    surface = White,              // Tarjetas Blancas
    surfaceVariant = White,       // Inputs Blancos

    surfaceTint = White,

    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,
    onSurfaceVariant = DeepOcean, // Iconos oscuros

    outline = DarkTeal,            // Bordes oscuros
    error = Color(0xFFB00020)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}