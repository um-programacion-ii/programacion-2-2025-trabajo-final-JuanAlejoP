# 🔌 Proxy - TiqueTo Sistema de Gestión de Eventos

## 📌 Descripción

El Proxy es un **adaptador crítico** que actúa como intermediario entre el Backend y la infraestructura de la Cátedra. Es responsable de:

- ✅ Comunicarse con los servicios de la Cátedra vía HTTP/REST
- ✅ Escuchar eventos en tiempo real a través de Kafka
- ✅ Almacenar y filtrar estado de asientos en Redis
- ✅ Sincronizar cambios de eventos con el Backend
- ✅ Gestionar tokens de autenticación Bearer

## 🏛️ Arquitectura Interna

El Proxy implementa **Arquitectura Hexagonal** con:

### **Driving Adapter (Entrada)**
```
listener/
└── KafkaConsumerListener.java
    - @KafkaListener(topics = "eventos-actualizacion")
    - Escucha cambios de eventos desde la Cátedra
```

### **Core (Dominio)**
```
Lógica de sincronización y procesamiento de eventos
- Parseo de mensajes Kafka
- Actualización de estado en Redis
- Notificación al Backend
```

### **Driven Adapters (Salida)**
```
service/
├── CatedraService.java       → Comunicación con API Cátedra (192.168.194.250)
├── RedisService.java         → Almacenamiento de estado de asientos
└── ProxyController.java      → Endpoints para Backend

controller/
└── ProxyController.java      → REST API (puerto 8081)
```

## 🚀 Ejecución

### **Ejecución en Desarrollo**
```bash

cd proxy
./mvnw spring-boot:run
```
✅ Iniciará en `http://localhost:8081`

### **Ejecución con Perfil Específico**
```bash

./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## 🔧 Configuración

### **Archivo: `src/main/resources/application.yaml`**

```yaml
server:
  port: 8081

spring:
  application:
    name: proxy
  
  # Redis Configuration (Cátedra)
  data:
    redis:
      host: 192.168.194.250
      port: 6379
      timeout: 2000ms
  
  # Kafka Configuration (Cátedra)
  kafka:
    bootstrap-servers: 192.168.194.250:9092
    consumer:
      group-id: juan-patino-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer

# Credenciales y configuración del Proxy
proxy:
  api-key: "backend-proxy-waguri"
  catedra-token: "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFuYWxlam9wIiwiZXhwIjoxNzY5Mjc2MDgyLCJhdXRoIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzY2Njg0MDgyfQ.hhp3EWWhtw9ei4pCM38LkoOIJKa1B62cvMJtHzp_4VOrplRlcGRhXVt2i6-9-E__uYFE3zmey7JtjWDIG_9UYQ"
```

## 📡 Flujo de Comunicación

```
1. BACKEND envía request (X-API-KEY)
   ↓
2. PROXY procesa request
   ↓
3. PROXY consulta CÁTEDRA (Token Bearer)
   ↓
4. PROXY almacena en REDIS
   ↓
5. PROXY responde al BACKEND

6. CÁTEDRA envía evento a KAFKA (eventos-actualizacion)
   ↓
7. KAFKA LISTENER recibe mensaje
   ↓
8. PROXY actualiza REDIS
   ↓
9. PROXY notifica al BACKEND (POST /api/eventos/sincronizar)
```

## 📊 Servicios Externos Requeridos

### **Cátedra API**
- **Host**: `192.168.194.250:8080`
- **Autenticación**: Token Bearer
- **Comunicación**: HTTP/REST

### **Kafka**
- **Host**: `192.168.194.250:9092`
- **Tópicos escuchados**: `eventos-actualizacion`
- **Deserialization**: String

### **Redis**
- **Host**: `192.168.194.250:6379`
- **Uso**: Almacenamiento de estado de asientos
- **TTL**: Configurable por evento

## 🔌 Endpoints del Proxy (Puerto 8081)

| Método | Endpoint | Autenticación | Descripción |
|--------|----------|---|-------------|
| GET | `/api/proxy/eventos/{id}/asientos` | X-API-KEY | Obtener asientos disponibles |
| POST | `/api/proxy/bloquear` | X-API-KEY | Bloquear asientos |
| POST | `/api/proxy/vender` | X-API-KEY | Procesar venta |
| GET | `/api/proxy/eventos-full` | X-API-KEY | Listar eventos con detalles |
| POST | `/api/eventos/sincronizar` | X-API-KEY | Sincronizar cambios (interno) |

## 🔑 Seguridad

### **API Key para Backend**
```
Header: X-API-KEY
Value: backend-proxy-waguri
```

### **Token Bearer para Cátedra**
```
Header: Authorization: Bearer <token>
Token (JWT): Especificado en application.yaml
```

## 🧪 Testing

Verificar conectividad con Kafka:
```bash

# Conectar a Kafka
kafka-console-consumer --bootstrap-servers 192.168.194.250:9092 --topic eventos-actualizacion
```

Verificar conectividad con Redis:
```bash

# Conectar a Redis
redis-cli -h 192.168.194.250 -p 6379 ping
```

Testear endpoint del Proxy:
```bash

curl -H "X-API-KEY: backend-proxy-waguri" \
  http://localhost:8081/api/proxy/eventos/1/asientos
```

## 📋 Estructura del Código

```
src/main/java/com/juanalejop/proxy/
├── ProxyApplication.java              # Punto de entrada Spring Boot
├── listener/
│   └── KafkaConsumerListener.java     # Consumer de Kafka
├── service/
│   ├── CatedraService.java            # Comunicación con Cátedra API
│   └── RedisService.java              # Gestión de Redis
├── controller/
│   └── ProxyController.java           # REST endpoints
└── dto/
    └── *.java                         # Objetos de transferencia
```

## ⚠️ Dependencias Críticas

- **Kafka**: DEBE estar disponible (192.168.194.250:9092)
- **Redis**: DEBE estar disponible (192.168.194.250:6379)
- **Cátedra API**: DEBE estar disponible (192.168.194.250:8080)
- **ZeroTier**: Conexión a la red privada de la Cátedra

Si falta alguno de estos servicios, el Proxy no funcionará correctamente.

## 🛠️ Stack Tecnológico

- **Framework**: Spring Boot 3.5.7
- **Messaging**: Spring Kafka
- **Cache**: Spring Data Redis
- **REST**: Spring Web
- **Build**: Maven
- **Java**: JDK 21

## 🔄 Ciclo de Vida

1. **Inicio**: Conectarse a Kafka, Redis y Cátedra
2. **Espera**: Escuchar mensajes en Kafka
3. **Recepción**: Cuando llega un evento de Cátedra
4. **Procesamiento**: Actualizar Redis
5. **Sincronización**: Notificar al Backend
6. **Respuesta**: Cuando Backend consulta asientos, devolver desde Redis