package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetalleScreen(
    eventoId: Long,
    onBack: () -> Unit,
    onComprarClick: () -> Unit
) {
    var evento by remember { mutableStateOf<Evento?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val repository = remember { EventosRepository() }

    LaunchedEffect(eventoId) {
        scope.launch {
            isLoading = true
            repository.getEvento(eventoId).onSuccess {
                evento = it
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(evento?.titulo ?: "Cargando...") }, // Título dinámico
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (evento != null) {
                Button(
                    onClick = onComprarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                ) {
                    Text("Seleccionar Asientos")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (evento != null) {
                val item = evento!! // Smart cast helper

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. BANNER DE IMAGEN 🖼️
                    if (!item.imagenUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = item.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {

                        // 2. TIPO Y FECHA
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            item.eventoTipo?.let { tipo ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(tipo.nombre) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            // Formato rápido de fecha (solo los primeros 10 chars: YYYY-MM-DD)
                            AssistChip(
                                onClick = {},
                                label = { Text(item.fechaHora.take(10)) },
                                leadingIcon = { Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. TÍTULO Y PRECIO
                        Text(item.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "$${item.precio}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // 4. DIRECCIÓN 📍
                        if (!item.direccion.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item.direccion, style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // 5. DESCRIPCIÓN
                        Text("Sobre el evento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.descripcion ?: "Sin descripción disponible.", style = MaterialTheme.typography.bodyMedium)

                        // 6. INTEGRANTES / ELENCO 🎭
                        if (!item.integrantes.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Presentadores", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            item.integrantes.forEach { integrante ->
                                ListItem(
                                    headlineContent = { Text("${integrante.nombre} ${integrante.apellido}") },
                                    supportingContent = if (!integrante.identificacion.isNullOrEmpty()) {
                                        { Text(integrante.identificacion) }
                                    } else null,
                                    leadingContent = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    }
                                )
                            }
                        }

                        // Espacio extra al final para que no tape el botón flotante
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}