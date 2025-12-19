package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanalejop.movil.data.model.Asiento
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.EventosRepository
import com.juanalejop.movil.ui.components.AsientoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaAsientosScreen(
    eventoId: Long,
    onBack: () -> Unit,
    onContinuar: (List<Asiento>) -> Unit
) {
    var eventoState by remember { mutableStateOf<Evento?>(null) }
    var selectedAsientos by remember { mutableStateOf<Set<Asiento>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { EventosRepository() }

    LaunchedEffect(eventoId) {
        scope.launch {
            isLoading = true
            errorMessage = null

            repository.getEvento(eventoId).onSuccess { eventoDescargado ->
                eventoState = eventoDescargado
                isLoading = false
            }.onFailure { e ->
                isLoading = false
                errorMessage = "Error cargando asientos: ${e.message ?: "Error de red"}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Asientos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onContinuar(selectedAsientos.toList()) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = selectedAsientos.isNotEmpty()
            ) {
                Text("Continuar (${selectedAsientos.size} seleccionados)")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌ Fallo al cargar asientos", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Volver e intentar de nuevo")
                    }
                }
            } else if (eventoState != null) {
                val evento = eventoState!!

                val filas = evento.filaAsientos ?: 10
                val columnas = evento.columnAsientos ?: 10
                val totalAsientos = filas * columnas

                val ocupadosMap = remember(evento.asientos) {
                    evento.asientos?.associateBy { "${it.fila}-${it.columna}" } ?: emptyMap()
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pantalla (Escenario)",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columnas),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(totalAsientos) { index ->
                            val filaActual = (index / columnas) + 1
                            val columnaActual = (index % columnas) + 1

                            val asientoOcupado = ocupadosMap["$filaActual-$columnaActual"]

                            val asientoADibujar = if (asientoOcupado != null) {
                                asientoOcupado
                            } else {
                                Asiento(filaActual, columnaActual, "Libre")
                            }

                            val isSelected = selectedAsientos.any { it.fila == filaActual && it.columna == columnaActual }

                            AsientoItem(
                                asiento = asientoADibujar,
                                isSelected = isSelected,
                                onClick = {
                                    if (asientoADibujar.estado == "Libre") {
                                        if (isSelected) {
                                            selectedAsientos = selectedAsientos.filterNot {
                                                it.fila == filaActual && it.columna == columnaActual
                                            }.toSet()
                                        } else {
                                            if (selectedAsientos.size < 4) {
                                                selectedAsientos = selectedAsientos + asientoADibujar
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}