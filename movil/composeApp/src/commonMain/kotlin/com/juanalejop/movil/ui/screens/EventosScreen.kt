package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp // Icono de Salir
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.AuthRepository
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    onEventoClick: (Long) -> Unit,
    onLogout: () -> Unit // 🆕 Callback para navegar al Login
) {
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { EventosRepository() }
    val authRepository = remember { AuthRepository() } // 🆕 Para hacer logout

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            repository.getEventos()
                .onSuccess { lista ->
                    eventos = lista
                    isLoading = false
                }
                .onFailure { error ->
                    // Si falla por 401 (Token vencido), hacemos logout forzado
                    if (error.message?.contains("401") == true) {
                        authRepository.logout()
                        onLogout()
                    } else {
                        errorMessage = "Error: ${error.message}"
                    }
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos Disponibles") },
                // 🆕 BOTÓN DE LOGOUT
                actions = {
                    IconButton(onClick = {
                        authRepository.logout() // Borra token
                        onLogout() // Navega al Login
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        // Botón de reintento o salida de emergencia
                        Button(onClick = {
                            authRepository.logout()
                            onLogout()
                        }) {
                            Text("Volver al Login")
                        }
                    }
                }

                eventos.isEmpty() -> Text("No se encontraron eventos.", modifier = Modifier.align(Alignment.Center))

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(eventos) { evento ->
                            EventoCard(evento, onClick = { onEventoClick(evento.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventoCard(evento: Evento, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título
            Text(text = evento.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            // Fecha (simple) y Precio
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Precio: $${evento.precio}", color = MaterialTheme.colorScheme.primary)
            }
            // Resumen
            if (!evento.resumen.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = evento.resumen, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}