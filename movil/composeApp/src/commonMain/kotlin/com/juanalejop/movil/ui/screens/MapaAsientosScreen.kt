package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    onContinuar: (List<Asiento>) -> Unit // Pasamos los seleccionados
) {
    var asientos by remember { mutableStateOf<List<Asiento>>(emptyList()) }
    var selectedAsientos by remember { mutableStateOf<Set<Asiento>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val repository = remember { EventosRepository() }

    LaunchedEffect(eventoId) {
        scope.launch {
            isLoading = true
            repository.getEvento(eventoId).onSuccess { evento ->
                // LÓGICA DE PRUEBA:
                // Si el backend no tiene asientos (H2 vacía), generamos 20 asientos falsos
                // para poder probar la UI visualmente.
                if (evento.asientos.isNullOrEmpty()) {
                    asientos = generarAsientosFalsos()
                } else {
                    asientos = evento.asientos
                }
                isLoading = false
            }.onFailure {
                isLoading = false
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
            // Botón inferior para continuar
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
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pantalla (Escenario)",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )

                    // GRILLA DE ASIENTOS
                    // Usamos una grilla de 4 columnas como ejemplo
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(asientos) { asiento ->
                            val isSelected = selectedAsientos.contains(asiento)
                            AsientoItem(
                                asiento = asiento,
                                isSelected = isSelected,
                                onClick = {
                                    // Lógica de selección (Máximo 4)
                                    if (isSelected) {
                                        selectedAsientos = selectedAsientos - asiento
                                    } else {
                                        if (selectedAsientos.size < 4) {
                                            selectedAsientos = selectedAsientos + asiento
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

// Función auxiliar para generar datos de prueba si el backend está vacío
fun generarAsientosFalsos(): List<Asiento> {
    val lista = mutableListOf<Asiento>()
    for (f in 1..5) {
        for (c in 1..4) {
            val estado = when {
                f == 2 && c == 2 -> "Vendido"    // Rojo
                f == 3 && c == 3 -> "Bloqueado"  // Gris (¡Nuevo!)
                else -> "Libre"                  // Verde
            }
            lista.add(Asiento(f, c, estado))
        }
    }
    return lista
}