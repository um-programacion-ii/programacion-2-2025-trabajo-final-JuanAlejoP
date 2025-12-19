package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.AuthRepository
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    onEventoClick: (Long) -> Unit,
    onLogout: () -> Unit
) {
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { EventosRepository() }
    val authRepository = remember { AuthRepository() }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            repository.getEventos()
                .onSuccess { lista ->
                    eventos = lista
                    isLoading = false
                }
                .onFailure { error ->
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
                actions = {
                    IconButton(onClick = {
                        authRepository.logout()
                        onLogout()
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            if (!evento.imagenUrl.isNullOrEmpty()) {
                val placeholderColor = if (isSystemInDarkTheme())
                    com.juanalejop.movil.ui.theme.PlaceholderDark
                else
                    com.juanalejop.movil.ui.theme.SoftTeal

                Card(
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(containerColor = placeholderColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = evento.imagenUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = rememberVectorPainter(image = Icons.Default.BrokenImage)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                evento.eventoTipo?.let { tipo ->
                    Text(
                        text = tipo.nombre.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = evento.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Text(
                    text = "$${evento.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (!evento.resumen.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = evento.resumen,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}