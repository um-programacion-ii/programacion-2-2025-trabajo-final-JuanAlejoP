package com.juanalejop.movil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.juanalejop.movil.data.model.AsientoPersona
import com.juanalejop.movil.data.model.AsientoSimple
import com.juanalejop.movil.data.model.SolicitudBloqueo
import com.juanalejop.movil.data.model.SolicitudVenta
import com.juanalejop.movil.data.network.ReservasRepository
import com.juanalejop.movil.ui.screens.CargaDatosScreen
import com.juanalejop.movil.ui.screens.EventoDetalleScreen
import com.juanalejop.movil.ui.screens.EventosScreen
import com.juanalejop.movil.ui.screens.LoginScreen
import com.juanalejop.movil.ui.screens.MapaAsientosScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class CurrentScreen {
    LOGIN, HOME, DETALLE, MAPA, CARGA_DATOS
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val reservasRepository = remember { ReservasRepository() }

        var currentScreen by remember { mutableStateOf(CurrentScreen.LOGIN) }
        var selectedEventoId by remember { mutableStateOf<Long?>(null) }
        var asientosSeleccionados by remember { mutableStateOf<List<com.juanalejop.movil.data.model.Asiento>>(emptyList()) }

        when (currentScreen) {
            CurrentScreen.LOGIN -> {
                LoginScreen(onLoginSuccess = { currentScreen = CurrentScreen.HOME })
            }
            CurrentScreen.HOME -> {
                EventosScreen(onEventoClick = { id ->
                    selectedEventoId = id
                    currentScreen = CurrentScreen.DETALLE
                })
            }
            CurrentScreen.DETALLE -> {
                if (selectedEventoId != null) {
                    EventoDetalleScreen(
                        eventoId = selectedEventoId!!,
                        onBack = { currentScreen = CurrentScreen.HOME },
                        onComprarClick = { currentScreen = CurrentScreen.MAPA }
                    )
                }
            }
            CurrentScreen.MAPA -> {
                // --- ISSUE 6.2: LÓGICA DE BLOQUEO ---
                if (selectedEventoId != null) {
                    MapaAsientosScreen(
                        eventoId = selectedEventoId!!,
                        onBack = { currentScreen = CurrentScreen.DETALLE },
                        onContinuar = { seleccion ->
                            scope.launch {
                                val solicitud = SolicitudBloqueo(
                                    eventoId = selectedEventoId!!,
                                    asientos = seleccion.map { AsientoSimple(it.fila, it.columna) }
                                )
                                println("Enviando bloqueo...")
                                reservasRepository.bloquearAsientos(solicitud)
                                    .onSuccess {
                                        println("Bloqueo OK. Pasando a carga de datos.")
                                        asientosSeleccionados = seleccion
                                        currentScreen = CurrentScreen.CARGA_DATOS
                                    }
                                    .onFailure {
                                        println("Error Bloqueo: ${it.message}")
                                    }
                            }
                        }
                    )
                }
            }
            CurrentScreen.CARGA_DATOS -> {
                // --- ISSUE 6.3: LÓGICA DE VENTA ---
                CargaDatosScreen(
                    asientos = asientosSeleccionados,
                    onBack = { currentScreen = CurrentScreen.MAPA },
                    onConfirmarCompra = { mapaDatos ->
                        scope.launch {
                            // 1. Transformar el mapa (Asiento -> Nombre) a la lista que pide el backend
                            val listaAsientosVenta = mapaDatos.map { (asiento, nombre) ->
                                AsientoPersona(asiento.fila, asiento.columna, nombre)
                            }

                            val solicitud = SolicitudVenta(
                                eventoId = selectedEventoId!!,
                                asientos = listaAsientosVenta
                            )

                            println("Enviando venta...")
                            reservasRepository.realizarVenta(solicitud)
                                .onSuccess {
                                    println("¡VENTA EXITOSA!")
                                    // Volver al inicio tras la compra
                                    currentScreen = CurrentScreen.HOME
                                }
                                .onFailure {
                                    println("Error Venta: ${it.message}")
                                }
                        }
                    }
                )
            }
        }
    }
}