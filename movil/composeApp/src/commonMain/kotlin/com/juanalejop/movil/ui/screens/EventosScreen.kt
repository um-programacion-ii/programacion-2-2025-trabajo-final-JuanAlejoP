package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(onEventoClick: (Long) -> Unit) {
    // Estados de la pantalla
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { EventosRepository() }

    // Cargar eventos apenas se abre la pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            repository.getEventos()
                .onSuccess { lista ->
                    eventos = lista
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = "Error: ${error.message}"
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Eventos Disponibles") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                errorMessage != null -> Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )

                eventos.isEmpty() -> Text("No se encontraron eventos.", modifier = Modifier.align(Alignment.Center))

                else -> {
                    // La Lista (LazyColumn)
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