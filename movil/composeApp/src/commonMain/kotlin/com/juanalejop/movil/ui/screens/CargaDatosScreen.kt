package com.juanalejop.movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juanalejop.movil.data.model.Asiento
import com.juanalejop.movil.ui.viewmodel.CargaDatosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaDatosScreen(
    asientos: List<Asiento>,
    onBack: () -> Unit,
    onConfirmarCompra: (Map<Asiento, String>) -> Unit,
    viewModel: CargaDatosViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.resetForm()
    }
    val nombresState = viewModel.nombresState
    val isFormValid = viewModel.isFormValid(asientos.size)

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
                    val resultado = viewModel.prepararDatosCompra(asientos)
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
                                viewModel.updateNombre(index, nuevoNombre)
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