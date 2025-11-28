package com.juanalejop.movil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.juanalejop.movil.ui.screens.LoginScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

// Enum simple para manejar pantallas sin librerías complejas por ahora
enum class CurrentScreen {
    LOGIN,
    HOME
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(CurrentScreen.LOGIN) }

        when (currentScreen) {
            CurrentScreen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = CurrentScreen.HOME
                    }
                )
            }
            CurrentScreen.HOME -> {
                androidx.compose.material3.Text("¡Estás logueado! Acá irán los eventos.")
            }
        }
    }
}