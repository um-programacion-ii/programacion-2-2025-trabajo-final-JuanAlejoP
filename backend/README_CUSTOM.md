# ⚙️ Backend - TiqueTo Sistema de Gestión de Eventos

## 📌 Descripción

Este módulo implementa el **núcleo de negocio** del sistema TiqueTo. Es responsable de:

- ✅ Gestionar eventos y usuarios
- ✅ Orquestar la lógica de compra y reserva de asientos
- ✅ Comunicarse con el Proxy para consultar disponibilidad en tiempo real
- ✅ Validar bloqueos temporales y procesar ventas
- ✅ Mantener autenticación y autorización con JWT

## 🏛️ Arquitectura Interna

El Backend implementa **Arquitectura Hexagonal (Pragmática)** con las siguientes capas:

### **Driving Adapters (Entrada - HTTP)**
```
web/rest/
├── EventoResource.java       → GET/POST eventos
├── ReservaController.java    → Bloqueo y venta de asientos
├── VentaResource.java        → Gestión de ventas
├── TicketResource.java       → Gestión de tickets
├── UserResource.java         → Gestión de usuarios
└── AuthenticateController.java → Autenticación JWT
```

### **Core (Dominio - Lógica Pura)**
```
service/
├── EventoService.java        → Lógica de eventos (agnóstica a HTTP)
├── VentaService.java         → Lógica de ventas
├── TicketService.java        → Lógica de tickets
└── UserService.java          → Lógica de usuarios
```

### **Driven Adapters (Salida)**
```
Persistencia (BD H2):
repository/
├── EventoRepository.java
├── UserRepository.java
├── VentaRepository.java
└── TicketRepository.java

Infraestructura Externa:
service/
└── ProxyService.java         → Comunicación con Proxy (RestTemplate)
```

### **Mappers (DTO ↔ Entity)**
```
service/mapper/
├── EventoMapper.java
├── UserMapper.java
└── VentaMapper.java
```

## 🚀 Ejecución

### **Ejecución en Desarrollo**
```bash

cd backend
./mvnw -Pdev spring-boot:run
```
✅ Iniciará en `http://localhost:8080`

### **Ejecución en Producción**
```bash

cd backend
./mvnw -Pprod clean verify
java -jar target/backend-*.jar
```

## 🔧 Configuración

### **Archivo: `src/main/resources/config/application-dev.yml`**

```yaml
server:
  port: 8080
  forward-headers-strategy: native

spring:
  datasource:
    url: jdbc:h2:mem:backend;DB_CLOSE_DELAY=-1
    username: backend
    password:
  
  # Comunicación con Proxy
  integration:
    proxy:
      url: http://localhost:8081/api/proxy
      api-key: "backend-proxy-waguri"

# Autenticación JWT
jhipster:
  security:
    authentication:
      jwt:
        token-validity-in-seconds: 1800  # 30 minutos
```

## 📊 Base de Datos

- **Motor**: H2 (en memoria para desarrollo)
- **Migraciones**: Liquibase (en `src/main/resources/db/changelog/`)
- **Entidades principales**:
  - `Evento` - Información de eventos
  - `User` - Usuarios del sistema
  - `Venta` - Transacciones de compra
  - `Ticket` - Entradas compradas

## 🔑 Autenticación

- **Tipo**: JWT (JSON Web Token)
- **Header**: `Authorization: Bearer <token>`
- **Generación**: Endpoint `POST /api/authenticate`
- **Validación**: Decorador `@Secured("ROLE_USER")`

## 📡 Comunicación con Proxy

El Backend se comunica con el Proxy (`puerto 8081`) para:

1. **Obtener asientos disponibles**: `GET /api/proxy/eventos/{id}/asientos`
2. **Bloquear asientos**: `POST /api/proxy/bloquear`
3. **Realizar venta**: `POST /api/proxy/vender`
4. **Obtener lista de eventos**: `GET /api/proxy/eventos-full`

**Header requerido**: `X-API-KEY: backend-proxy-waguri`

## 🧪 Testing

Ejecutar tests unitarios:
```bash

./mvnw test
```

Ejecutar tests de integración:
```bash

./mvnw verify
```

## 📋 Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/authenticate` | Login |
| GET | `/api/eventos` | Listar eventos |
| GET | `/api/eventos/{id}` | Detalle evento (con asientos) |
| POST | `/api/reservas/bloquear` | Bloquear asientos |
| POST | `/api/reservas/vender` | Comprar entradas |
| GET | `/api/account` | Perfil actual |

## 🛠️ Stack Tecnológico

- **Framework**: Spring Boot 3.4.5
- **ORM**: JPA/Hibernate
- **Base de Datos**: H2
- **Migraciones**: Liquibase
- **REST**: Spring Web
- **Seguridad**: Spring Security + JWT
- **Build**: Maven
- **Generador**: JHipster 8.11.0
