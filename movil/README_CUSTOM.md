# 📱 Móvil - TiqueTo Sistema de Gestión de Eventos

## 📌 Descripción

Este módulo implementa la **interfaz de usuario móvil** del sistema TiqueTo usando **Kotlin Multiplatform** y **Jetpack Compose**. Es responsable de:

- ✅ Autenticación de usuarios (Login/Registro)
- ✅ Visualización de eventos disponibles
- ✅ Mapa interactivo de asientos
- ✅ Bloqueo temporal de asientos
- ✅ Procesamiento de compra de entradas
- ✅ UI reactiva y responsiva

## 🏛️ Arquitectura Interna

El módulo móvil implementa el patrón **MVVM (Model-View-ViewModel)** con arquitectura limpia:

### **Capas**

```
Presentación (UI)
├── Screens/
│   ├── LoginScreen.kt          → Autenticación
│   ├── RegisterScreen.kt       → Registro de usuarios
│   ├── EventosScreen.kt        → Listado de eventos
│   ├── EventoDetalleScreen.kt  → Detalle de evento
│   ├── MapaAsientosScreen.kt   → Mapa interactivo
│   ├── CargaDatosScreen.kt     → Pantalla de carga
│   └── CompraExitosaScreen.kt  → Confirmación
│
└── Components/
    └── Componentes reutilizables de Compose

State Management (ViewModel)
├── LoginViewModel.kt           → Lógica de autenticación
├── RegisterViewModel.kt        → Lógica de registro
├── EventosViewModel.kt         → Lógica de eventos
├── EventoDetalleViewModel.kt   → Detalle y asientos
├── MapaAsientosViewModel.kt    → Manejo del mapa
└── CargaDatosViewModel.kt      → Gestión de carga

Data Layer (Repository)
├── AuthRepository.kt           → Autenticación
├── EventosRepository.kt        → Eventos
└── ReservasRepository.kt       → Reservas y compras

Network
├── KtorClient.kt              → Cliente HTTP (Ktor)
└── Interceptores              → Manejo de JWT
```

## 🚀 Ejecución

### **Compilación para Android**

**Debug (Desarrollo):**
```bash

cd movil
./gradlew :composeApp:assembleDebug
```

**Release (Producción):**
```bash

./gradlew :composeApp:assembleRelease
```

### **Ejecución en Emulador/Dispositivo**

Desde Android Studio:
1. Seleccionar "Run configuration" → composeApp
2. Elegir emulador o dispositivo conectado
3. Click en "Run"

O desde terminal:
```bash

./gradlew :composeApp:installDebug
```

### **Compilación para iOS**

Desde Xcode:
1. Abrir `/iosApp/iosApp.xcodeproj`
2. Seleccionar target "iosApp"
3. Click en "Run"

## 🏗️ Estructura del Código

```
composeApp/src/
├── commonMain/kotlin/com/juanalejop/movil/
│   ├── App.kt                         # Punto de entrada y navegación
│   ├── Greeting.kt                    # Composables de ejemplo
│   ├── Platform.kt                    # Abstracciones multiplataforma
│   │
│   ├── ui/
│   │   ├── screens/                   # Pantallas de la app
│   │   │   ├── LoginScreen.kt
│   │   │   ├── RegisterScreen.kt
│   │   │   ├── EventosScreen.kt
│   │   │   ├── EventoDetalleScreen.kt
│   │   │   ├── MapaAsientosScreen.kt
│   │   │   ├── CargaDatosScreen.kt
│   │   │   └── CompraExitosaScreen.kt
│   │   │
│   │   ├── viewmodel/                 # State Management
│   │   │   ├── LoginViewModel.kt
│   │   │   ├── RegisterViewModel.kt
│   │   │   ├── EventosViewModel.kt
│   │   │   ├── EventoDetalleViewModel.kt
│   │   │   ├── MapaAsientosViewModel.kt
│   │   │   └── CargaDatosViewModel.kt
│   │   │
│   │   ├── components/                # Componentes reutilizables
│   │   │   └── *.kt
│   │   │
│   │   └── theme/                     # Diseño y estilos
│   │       ├── Color.kt
│   │       ├── Type.kt
│   │       └── AppTheme.kt
│   │
│   ├── data/
│   │   ├── model/                     # Modelos de datos
│   │   │   ├── Asiento.kt
│   │   │   ├── Evento.kt
│   │   │   ├── Usuario.kt
│   │   │   └── Venta.kt
│   │   │
│   │   └── repository/                # Repositorios (acceso a datos)
│   │       ├── AuthRepository.kt
│   │       ├── EventosRepository.kt
│   │       └── ReservasRepository.kt
│   │
│   └── network/
│       ├── KtorClient.kt              # Cliente HTTP
│       ├── AuthInterceptor.kt         # Manejo de JWT
│       └── ApiConfig.kt               # Configuración de API
│
├── androidMain/kotlin/                # Código específico Android
├── iosMain/kotlin/                    # Código específico iOS
└── commonTest/kotlin/                 # Tests compartidos
```

## 🔐 Autenticación

### **Token JWT**
```kotlin
// Almacenado localmente después del login
val token = "eyJhbGciOiJIUzUxMiJ9..."

// Enviado en cada request
header("Authorization", "Bearer $token")
```

### **Flow de Autenticación**
```
1. Usuario ingresa credenciales
   ↓
2. LoginViewModel → AuthRepository
   ↓
3. POST /api/authenticate (Backend 8080)
   ↓
4. Backend retorna JWT
   ↓
5. Guardar token localmente
   ↓
6. Usar token en requests posteriores
```

## 📡 Comunicación con Backend

### **Base URL**
```kotlin
const val BASE_URL = "http://192.168.x.x:8080"  // IP del Backend
```

### **Endpoints Utilizados**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/authenticate` | Login |
| POST | `/api/register` | Registro |
| GET | `/api/eventos` | Listar eventos |
| GET | `/api/eventos/{id}` | Detalle evento |
| POST | `/api/reservas/bloquear` | Bloquear asientos |
| POST | `/api/reservas/vender` | Comprar entradas |
| GET | `/api/account` | Perfil del usuario |

## 🎨 UI/UX

### **Patrón MVVM**
- **Screen**: Composables puros (sin lógica)
- **ViewModel**: Gestión de estado (StateFlow, SharedFlow)
- **Repository**: Acceso a datos

### **Estado Reactivo**
```kotlin
class EventosViewModel {
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()
    
    fun loadEventos() {
        // Cargar desde API
    }
}
```

### **Navegación**
```kotlin
enum class CurrentScreen {
    LOGIN,           // Pantalla inicial
    REGISTER,        // Registro
    HOME,            // Listado de eventos
    DETALLE,         // Detalle de evento
    MAPA,            // Mapa de asientos
    CARGA_DATOS,     // Carga
    COMPRA_EXITOSA   // Confirmación
}
```

## 🎯 Flujo de Usuario Típico

```
1. LOGIN SCREEN
   ├─ Ingresa credenciales
   └─ POST /api/authenticate

2. HOME SCREEN (Eventos)
   ├─ GET /api/eventos
   └─ Muestra lista

3. DETALLE SCREEN
   ├─ GET /api/eventos/{id}
   └─ Muestra info + asientos

4. MAPA SCREEN
   ├─ Selecciona asientos
   └─ Muestra mapa interactivo

5. CARGA DATA SCREEN
   ├─ POST /api/reservas/bloquear
   └─ Bloquea asientos

6. COMPRA EXITOSA
   ├─ POST /api/reservas/vender
   └─ Procesa venta

7. CONFIRMACIÓN
   └─ Muestra entradas compradas
```

## 🔧 Configuración

### **Archivo: `local.properties`**

```properties
# IP del Backend (cambiar según tu red)
backend.url=http://192.168.x.x:8080
```

### **Archivo: `gradle/libs.versions.toml`**

Dependencias principales:
```toml
[versions]
kotlin = "2.x.x"
compose = "1.x.x"
ktor = "3.x.x"
```

## 📦 Dependencias Principales

- **Kotlin Multiplatform**: Código compartido Android/iOS
- **Jetpack Compose**: UI declarativa
- **Ktor Client**: Cliente HTTP
- **Kotlinx Serialization**: Serialización JSON
- **Coroutines**: Async/await

## 🧪 Testing

Tests unitarios (ViewModel):
```bash
./gradlew :composeApp:testDebug
```

## ⚠️ Requisitos

- **Android**: Mínimo API 21 (Android 5.0)
- **iOS**: Mínimo iOS 12.0
- **Conectividad**: Acceso a Backend en puerto 8080
- **Permiso de Red**: AndroidManifest.xml requiere `INTERNET`

## 🚀 Build Variantes

```bash
# Debug
./gradlew :composeApp:assembleDebug

# Release (optimizado para producción)
./gradlew :composeApp:assembleRelease

# Universal APK (todos los ABIs)
./gradlew :composeApp:bundleRelease
```

## 📱 Plataformas Soportadas

- ✅ Android (API 21+)
- ✅ iOS (12.0+)