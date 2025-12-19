package com.juanalejop.movil

import androidx.compose.runtime.*
import com.juanalejop.movil.data.model.AsientoPersona
import com.juanalejop.movil.data.model.AsientoSimple
import com.juanalejop.movil.data.model.SolicitudBloqueo
import com.juanalejop.movil.data.model.SolicitudVenta
import com.juanalejop.movil.data.network.ReservasRepository
import com.juanalejop.movil.ui.screens.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.juanalejop.movil.ui.theme.AppTheme
enum class CurrentScreen {
    LOGIN, HOME, DETALLE, MAPA, CARGA_DATOS, COMPRA_EXITOSA
}

@Composable
@Preview
fun App() {
    AppTheme {
        val scope = rememberCoroutineScope()
        val reservasRepository = remember { ReservasRepository() }

        var currentScreen by remember { mutableStateOf(CurrentScreen.LOGIN) }
        var selectedEventoId by remember { mutableStateOf<Long?>(null) }

        // 🆕 VARIABLE NUEVA PARA EL TÍTULO
        var selectedEventoTitulo by remember { mutableStateOf("") }

        var asientosSeleccionados by remember { mutableStateOf<List<com.juanalejop.movil.data.model.Asiento>>(emptyList()) }
        var entradasCompradas by remember { mutableStateOf<List<AsientoPersona>>(emptyList()) }

        when (currentScreen) {
            CurrentScreen.LOGIN -> {
                LoginScreen(onLoginSuccess = { currentScreen = CurrentScreen.HOME })
            }
            CurrentScreen.HOME -> {
                EventosScreen(
                    onEventoClick = { id ->
                        selectedEventoId = id
                        currentScreen = CurrentScreen.DETALLE
                    },
                    onLogout = {
                        selectedEventoId = null
                        selectedEventoTitulo = "" // Limpiamos título
                        asientosSeleccionados = emptyList()
                        entradasCompradas = emptyList()
                        currentScreen = CurrentScreen.LOGIN
                    }
                )
            }
            CurrentScreen.DETALLE -> {
                if (selectedEventoId != null) {
                    EventoDetalleScreen(
                        eventoId = selectedEventoId!!,
                        onBack = { currentScreen = CurrentScreen.HOME },
                        // 🆕 CAPTURAMOS EL TÍTULO AQUÍ
                        onComprarClick = { titulo ->
                            selectedEventoTitulo = titulo
                            currentScreen = CurrentScreen.MAPA
                        }
                    )
                }
            }
            CurrentScreen.MAPA -> { /* (Sin cambios, igual que antes) */
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
                                reservasRepository.bloquearAsientos(solicitud)
                                    .onSuccess {
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
            CurrentScreen.CARGA_DATOS -> { /* (Sin cambios, igual que antes) */
                CargaDatosScreen(
                    asientos = asientosSeleccionados,
                    onBack = { currentScreen = CurrentScreen.MAPA },
                    onConfirmarCompra = { mapaDatos ->
                        scope.launch {
                            val listaAsientosVenta = mapaDatos.map { (asiento, nombre) ->
                                AsientoPersona(asiento.fila, asiento.columna, nombre)
                            }
                            val solicitud = SolicitudVenta(
                                eventoId = selectedEventoId!!,
                                asientos = listaAsientosVenta
                            )
                            reservasRepository.realizarVenta(solicitud)
                                .onSuccess {
                                    entradasCompradas = listaAsientosVenta
                                    currentScreen = CurrentScreen.COMPRA_EXITOSA
                                }
                                .onFailure {
                                    println("Error Venta: ${it.message}")
                                }
                        }
                    }
                )
            }
            CurrentScreen.COMPRA_EXITOSA -> {
                // 🆕 PASAMOS EL TÍTULO Y LA LISTA
                CompraExitosaScreen(
                    eventoTitulo = selectedEventoTitulo,
                    entradas = entradasCompradas,
                    onVolverInicio = {
                        selectedEventoId = null
                        selectedEventoTitulo = ""
                        asientosSeleccionados = emptyList()
                        entradasCompradas = emptyList()
                        currentScreen = CurrentScreen.HOME
                    }
                )
            }
        }
    }
}