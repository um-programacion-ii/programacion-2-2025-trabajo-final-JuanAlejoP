[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/IEOUmR9z)

# 🎟️ TiqueTo - Sistema de Gestión de Eventos

## 👨‍🎓 Información del Alumno
- **Nombre y Apellido:** Juan Alejo Patiño
- **Legajo:** 61160
- **Materia:** Programación 2 - Universidad de Mendoza (2025)

---

## 📋 Descripción del Proyecto

**TiqueTo** es una solución integral distribuida para la compra y reserva de entradas a eventos. El sistema permite visualizar eventos, sus detalles, seleccionar asientos en un mapa interactivo en tiempo real, gestionar bloqueos temporales y concretar ventas, integrándose con servicios externos de la Cátedra vía Kafka y Redis. 

Este proyecto fue desarrollado como **Trabajo Final** para la cátedra de Programación 2 (2025).

---

## 🏛️ Arquitectura del Sistema

El proyecto implementa una **arquitectura distribuida** compuesta por cuatro nodos principales. A continuación se detalla el flujo de comunicación y los puertos utilizados:

```text
┌──────────────────────┐
│   Servicio de        │
│    la Cátedra        │
│ (192.168.194.250)    │
└──────────────────────┘
        ▲                ▼
        │ HTTP/REST      │ Kafka (eventos-actualizacion)
        │ (Token Bearer) │
        │                ▼
        │          ┌──────────────────┐
        │          │  PROXY           │
        └─────────►│ (Spring Boot)    │
                   │ Puerto 8081      │
                   │                  │
                   │ ┌──────────────┐ │
                   │ │   Kafka      │ │  <-- Escucha eventos
                   │ │   Listener   │ │      de actualizacion
                   │ └──────────────┘ │
                   │        ▲         │
                   │        │         │
                   │ ┌──────▼──────┐  │
                   │ │   Redis     │  │  <-- Almacena y
                   │ │   Service   │  │      filtra estado
                   │ └─────────────┘  │      de asientos
                   └────────┬─────────┘
                            │ HTTP/REST (X-API-KEY)
                            │ (request/response)
                            ▼
                   ┌──────────────────┐
                   │    BACKEND       │
                   │  (Spring Boot)   │
                   │   Puerto 8080    │
                   │                  │
                   │ ┌──────────────┐ │
                   │ │ EventoService│ │  <-- Orquesta compras
                   │ │ (Dominio)    │ │      y reservas
                   │ │ ProxyService │ │      (Agnóstico a HTTP)
                   │ └──────────────┘ │
                   └────────▲─────────┘
                            │ HTTP/REST (JWT Auth)
                            │ (request/response)
                            ▼
                   ┌──────────────────┐
                   │      MÓVIL       │
                   │ (Kotlin + KMP)   │
                   │     Android      │
                   │                  │
                   │ ┌──────────────┐ │
                   │ │   MVVM       │ │  <-- UI Reactiva
                   │ │   Compose    │ │      con Screens
                   │ │   +          │ │      y ViewModels
                   │ │  Ktor Client │ │
                   │ └──────────────┘ │
                   └──────────────────┘
```

**Flujo de Datos Detallado:**

1. **Móvil ↔ Backend (JWT):** Login, obtener eventos, mapa de asientos
2. **Backend ↔ Proxy (X-API-KEY):** Consultar/bloquear/vender asientos
3. **Proxy ↔ Cátedra (Token Bearer):** Comunicación con servicios externos
4. **Cátedra → Kafka → Proxy:** Notificaciones de cambios en eventos
5. **Proxy → Redis:** Almacenamiento de estado de asientos en caché
6. **Proxy → Backend (POST):** Notificación de sincronización (`/api/eventos/sincronizar`)

### 🧩 Componentes Desarrollados por el Alumno

1.  **📱 Móvil (Kotlin Multiplatform / Jetpack Compose):**
    * Implementa patrón **MVVM** (Model-View-ViewModel) estricto.
    * Gestión de estados reactivos y navegación segura.
    * Funcionalidades: Registro, Login, Mapa de Asientos, Venta.

2.  **⚙️ Backend (Spring Boot / JHipster):**
    * **Puerto: 8080**
    * Núcleo de negocio. Gestiona usuarios, sesiones y orquesta la compra.
    * Implementa **Arquitectura Hexagonal (Pragmática)**: El dominio (`EventoService`) es agnóstico a la infraestructura externa, delegando la comunicación en adaptadores (`ProxyService`).

3.  **🔌 Proxy (Spring Boot):**
    * **Puerto: 8081**
    * Actúa como **Adaptador de Salida** hacia la infraestructura de la Cátedra.
    * Escucha tópicos de **Kafka** para sincronización en tiempo real.
    * Consulta y filtra estados de asientos en **Redis**.

### 🏛️ Implementación de Arquitectura Hexagonal (Pragmática)

En el Backend (JHipster), se aplica una arquitectura hexagonal pragmática donde el Dominio permanece agnóstico a la infraestructura externa, utilizando adaptadores para todas las comunicaciones externas.

#### **Backend - Arquitectura Hexagonal Detallada:**

```text
ENTRADA (Driving Adapters)          DOMINIO (Core)                  SALIDA (Driven Adapters)

┌──────────────────────────┐    ┌──────────────────────────┐     ┌───────────────────────┐
│   Controllers REST       │    │                          │     │  Base de Datos        │
│ ┌────────────────────┐   │    │    EventoService         │     │ ┌──────────────────┐  │
│ │ EventoResource     │   │    │   (Lógica Pura)          │     │ │ EventoRepository │  │
│ │ ReservaController  │   │    │                          │     │ │ UserRepository   │  │
│ │ VentaResource      │   │    │ - Orquesta Compras       │ ───►│ │ VentaRepository  │  │
│ │ TicketResource     │   │    │ - Gestiona Eventos       │     │ │ (JPA/H2)         │  │
│ │ UserResource       │   │    │ - Valida Bloqueos        │     │ └──────────────────┘  │
│ │ (JWT Authentication)   │    │                          │     │                       │
│ └────────────────────┘   │    │ NO conoce HTTP,          │     └───────────────────────┘
│                          │    │ NO maneja REST,          │
│          (request/response)   │ NO accede directo        │     ┌───────────────────────┐
└────────┬─────────────────┘    │ a infraestructura        │     │  Infraestructura      │
         │                      └──────────┬───────────────┘     │  Externa              │
         │                                 │                     │ ┌──────────────────┐  │
         │                                 │                     │ │ ProxyService     │  │
         │                                 │                     │ │ (RestTemplate)   │  │
         │                                 │                     │ │                  │  │
         │                                 │                     │ ├─ obtenerAsientos │  │
         │                                 │                     │ ├─ bloquearAsientos│  │
         │                                 └────────────────────►│ ├─ realizarVenta   │  │
         │                                                       │ └─ obtenerListaEventos│
         │                     (Mappers)                         │ └──────────────────┘  │
         │                  ┌──────────────┐                     │        ↓              │
         │                  │ EventoMapper │                     │  (HTTP/REST a Proxy)  │
         └─────────────────►│ UserMapper   │                     │  (X-API-KEY)          │
            (DTO ↔ Entity)  │ VentaMapper  │────────┐            └───────────────────────┘
                            └──────────────┘        │
                                                    ▼
                                            ┌──────────────┐
                                            │   Entidades  │
                                            │  (Domain)    │
                                            └──────────────┘

```
**Flujo Típico de una Compra:**
1. Cliente (Móvil) → EventoResource (GET /api/eventos/:id) [JWT Auth]
2. EventoResource → EventoMapper.toDto(evento)
3. EventoMapper → EventoService.findOne(id)
4. EventoService → EventoRepository.findById(id) [BD]
5. EventoService → ProxyService.obtenerAsientos(idCatedra) [HTTP]
6. ProxyService → Proxy Service (REST call con X-API-KEY)
7. Respuesta vuelve a través de las capas

**Responsabilidades por Capa:**

| Capa | Componentes | Responsabilidad |
|------|-------------|-----------------|
| **Entrada (Driving)** | EventoResource, ReservaController, VentaResource, TicketResource, UserResource | Traducir HTTP ↔ Dominio, autenticar (JWT), validar requests |
| **Mappers** | EventoMapper, UserMapper, VentaMapper | Convertir entre DTOs (HTTP) y Entidades (Dominio) |
| **Dominio (Core)** | EventoService, VentaService, TicketService, UserService | Lógica de negocio pura, orquestación, validaciones |
| **Salida (Driven)** | EventoRepository, UserRepository, VentaRepository, ProxyService | Persistencia (BD) y comunicación con infraestructura externa |

#### **Proxy - También implementa Arquitectura Hexagonal:**

```text
Kafka Topic (eventos-actualizacion)
        │
        ▼
┌──────────────────────────┐
│  KafkaConsumerListener   │  ◄── ENTRADA (Driving)
│  (@KafkaListener)        │      Escucha eventos de cambio
└────────────┬─────────────┘
             │
             ▼
    ┌─────────────────┐
    │  Redis Service  │      ◄── SALIDA (Driven - Lectura)
    │  Cátedra API    │           Almacena estado de asientos
    └─────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│   POST /api/eventos/sincronizar  │  ◄── SALIDA (Driven - Notificación)
│   (Notifica al Backend)          │      Envía cambios al Backend
└──────────────────────────────────┘
```

---

## 🔧 Requisitos Previos

Para ejecutar este proyecto se necesita tener instalado:

- **Java JDK 21** o superior.

- **Maven** (no requiere instalación global).

- **Android Studio** (para el cliente móvil)

- **ZeroTier** (para conectarse a la red de la cátedra)



Para que el sistema funcione correctamente (especialmente el Proxy), el equipo debe tener acceso a los servicios de la cátedra:

* **Red:** Conexión vía **ZeroTier** requerida.
* **API Cátedra:** `192.168.194.250:8080`
* **Kafka:** `192.168.194.250:9092`
* **Redis:** `192.168.194.250:6379`

---

## 🚀 Ejecución Rápida (Script Automatizado)

Se incluye un script de automatización que detecta su terminal y levanta los servicios necesarios (Backend y Proxy) automáticamente.

1.  Dar permisos de ejecución al script:
    ```bash
    chmod +x iniciar-sistema-eventos.sh
    ```

2.  Ejecutar script desde la raíz del proyecto:
    ```bash
    ./iniciar-sistema-eventos.sh
    ```

3.  Una vez iniciados los servicios, abrir **Android Studio** y ejecutar el módulo `movil` en emulador o dispositivo físico.

---

## 🛠️ Ejecución Manual

Si el script no funciona en su entorno, pueden levantarse los servicios  manualmente ejecutando en terminales separadas:

**Terminal 1: Backend**
```bash

cd backend
./mvnw -Pdev spring-boot:run
```
*Iniciará en el puerto `8080`.*

**Terminal 2: Proxy**
```bash

cd proxy
./mvnw spring-boot:run
```
*Iniciará en el puerto `8081`.*

---

## 👤 Credenciales de Prueba

Para ingresar a la aplicación móvil puede utilizar las siguientes credenciales (generadas por defecto en el entorno de desarrollo):

| Rol | Usuario | Contraseña |
| :--- | :--- | :--- |
| **Usuario** | `user` | `user` |
| **Admin** | `admin` | `admin` |

> También es posible registrar nuevos usuarios desde la App Móvil.

---

## 🐛 Solución de Problemas Comunes

**❌ Error de puertos ocupados / Port 8080 already in use**

**Síntoma:** No se puede iniciar el Backend o Proxy porque alguno de los puertos (`8080` o `8081`) ya está en uso.

**Soluciones:**
```bash

# Ver qué proceso usa el puerto
lsof -i :8080 #Linux/Mac
netstat -ano | findstr :8080 #Windows

# Matar el proceso
kill -9 <PID> #Linux/Mac
taskkill /PID <PID> /F #Windows
```

---

## 📊 Estructura del Proyecto

El código fuente está organizado en tres módulos principales:

```text
📦 programacion-2-2025-trabajo-final
├── 📂 backend/                 # Backend (Puerto 8080)
│   ├── 📂 src/main/java/com/juanalejop/backend/
│   │   ├── 📂 domain/       # Entidades JPA (Evento, Asiento)
│   │   ├── 📂 repository/   # Repositorios (Output Adapter DB)
│   │   ├── 📂 service/      # Lógica de Negocio
│   │   │   ├── 📂 dto/      # Objetos de Transferencia
│   │   │   ├── 📂 mapper/   # Mappers (DTO ↔ Entity)
│   │   │   ├── EventoService.java  # Core Logic
│   │   │   └── ProxyService.java   # Output Adapter (hacia Proxy)
│   │   └── 📂 web/rest/     # Controladores REST (Input Adapter)
│   └── mvnw
│
├── 📂 proxy/                   # Proxy (Puerto 8081)
│   ├── 📂 src/main/java/com/juanalejop/proxy/
│   │   ├── 📂 listener/     # Kafka Consumer (Input Adapter)
│   │   ├── 📂 service/      # Servicios de Redis y Cátedra
│   │   └── 📂 controller/   # Endpoints para el Backend
│   └── mvnw
│
├── 📂 movil/                   # Cliente Android
│   ├── 📂 composeApp/src/commonMain/kotlin/com/juanalejop/movil/
│   │   ├── 📂 ui/
│   │   │   ├── 📂 screens/      # Pantallas (Login, Mapa, etc.)
│   │   │   ├── 📂 viewmodel/    # State Management (MVVM)
│   │   │   ├── 📂 components/   # Componentes reutilizables Compose
│   │   │   └── 📂 theme/        # Configuración de temas y estilos
│   │   ├── 📂 data/             # Repositorios y Modelos
│   │   └── 📂 network/          # Cliente HTTP (Ktor)
│   └── build.gradle.kts
│
├── 📄 iniciar-sistema-eventos.sh  # Script de automatización
└── 📄 README.md                   # Documentación
```

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos para la Universidad de Mendoza.