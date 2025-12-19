package com.juanalejop.movil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta Clara (Modo Claro) ☀️
private val LightColorScheme = lightColorScheme(
    primary = DarkTeal,
    primaryContainer = ElectricTeal,
    secondary = DeepOcean,

    background = White,
    surface = White,
    surfaceVariant = White,

    surfaceTint = White,

    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,
    onSurfaceVariant = DeepOcean,

    outline = DarkTeal,
    error = Color(0xFFB00020)
)

// Paleta Oscura (Modo Oscuro) 🌙
private val DarkColorScheme = darkColorScheme(
    primary = ElectricTeal,
    primaryContainer = ElectricTeal,
    secondary = ElectricTeal,

    background = Black,
    surface = DarkSurface,
    surfaceVariant = DarkSurface,

    onPrimary = Black,
    onSecondary = Black,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = ElectricTeal,

    outline = ElectricTeal,
    error= Color(0xFFCF6679)
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