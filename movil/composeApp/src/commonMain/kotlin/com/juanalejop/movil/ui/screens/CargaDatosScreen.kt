package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanalejop.movil.data.model.Asiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaDatosScreen(
    asientos: List<Asiento>,
    onBack: () -> Unit,
    onConfirmarCompra: (Map<Asiento, String>) -> Unit // Devuelve Mapa: Asiento -> Nombre Persona
) {
    // Estado para guardar los nombres. Clave: Índice del asiento, Valor: Nombre
    val nombresState = remember { mutableStateMapOf<Int, String>() }

    // Validar si todos los campos tienen texto
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
                    // Preparamos el mapa final para enviar
                    val resultado = asientos.mapIndexed { index, asiento ->
                        asiento to (nombresState[index] ?: "")
                    }.toMap()
                    onConfirmarCompra(resultado)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = isFormValid // Solo habilita si llenó todo
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
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            items(asientos.size) { index ->
                val asiento = asientos[index]
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Asiento: Fila ${asiento.fila} - Columna ${asiento.columna}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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