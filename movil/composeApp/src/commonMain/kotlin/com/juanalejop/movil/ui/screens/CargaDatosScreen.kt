package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanalejop.movil.data.model.Asiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaDatosScreen(
    asientos: List<Asiento>,
    onBack: () -> Unit,
    onConfirmarCompra: (Map<Asiento, String>) -> Unit
) {
    val nombresState = remember { mutableStateMapOf<Int, String>() }

    val isFormValid = asientos.indices.all { i ->
        !nombresState[i].isNullOrBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos de los Asistentes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val resultado = asientos.mapIndexed { index, asiento ->
                        asiento to (nombresState[index] ?: "")
                    }.toMap()
                    onConfirmarCompra(resultado)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = isFormValid
            ) {
                Text("Confirmar Compra")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Por favor ingresa el nombre completo para cada entrada:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            items(asientos.size) { index ->
                val asiento = asientos[index]
                // Esta Card ahora será BLANCA automáticamente por el Theme
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Asiento: Fila ${asiento.fila} - Columna ${asiento.columna}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = nombresState[index] ?: "",
                            onValueChange = { nuevoNombre ->
                                nombresState[index] = nuevoNombre
                            },
                            label = { Text("Nombre y Apellido") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}