[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/IEOUmR9z)

## 👨‍🎓 Información del Alumno
- **Nombre y Apellido:** Juan Alejo Patiño
- **Legajo:** 61160

**Programación 2 - Universidad de Mendoza**

# 🎟️ TiqueTo - Sistema de Eventos

**TiqueTo** es una solución integral para la compra y reserva de entradas a eventos. El sistema permite a los usuarios visualizar eventos disponibles, sus detalles, seleccionar asientos en tiempo real mediante un mapa interactivo y gestionar sus compras de manera segura.

Este proyecto fue desarrollado como Trabajo Final para la cátedra de Programación 2 (2025).

---

## 🏗️ Arquitectura del Sistema

El proyecto está dividido en tres módulos principales que se comunican entre sí:

1.  **📱 Móvil (Android/Compose):**
    * Interfaz de usuario moderna construida con **Jetpack Compose**.
    * Manejo de estados, navegación y consumo de APIs REST.
    * Características: Login, Listado de Eventos, Mapa de Asientos Interactivo, Carga de Pasajeros.

2.  **⚙️ Backend (Spring Boot):**
    * Núcleo lógico del sistema.
    * Gestiona la base de datos de eventos y usuarios.
    * Orquesta la comunicación entre la App Móvil y el Proxy.

3.  **🔌 Proxy (Spring Boot + Redis/Kafka):**
    * Intermediario de alto rendimiento.
    * Simula la conexión con la API de la Cátedra (Sistemas Externos).
    * Maneja la concurrencia y el bloqueo de asientos.

---

## 📋 Requisitos Previos

Para ejecutar este proyecto se necesita tener instalado:

* **Java JDK 17** o superior.
* **Android Studio** (para correr la App Móvil).
* **Terminal Bash** (Linux/Mac) o Git Bash (Windows).

---

## 🚀 Ejecución Rápida (Recomendado)

Se incluye un script de automatización que detecta tu terminal y levanta los servicios necesarios (Backend y Proxy) automáticamente.

1.  Otorgar permisos de ejecución al script:
    ```bash
    chmod +x iniciar-sistema-eventos.sh
    ```

2.  Ejecutar el script desde la raíz del proyecto:
    ```bash
    ./iniciar-sistema-eventos.sh
    ```

3.  Una vez iniciados los servicios, abrir **Android Studio**, sincronizar el proyecto `movil` y ejecutar en emulador o dispositivo físico.

---

## 🛠️ Ejecución Manual

Si el script automático no funciona en el entorno, se pueden levantar los servicios manualmente abriendo dos terminales separadas en la raíz del proyecto:

**Terminal 1: Backend**
```bash
cd backend
./mvnw -Pdev spring-boot:run
```
*El backend iniciará en el puerto `8080`.*

**Terminal 2: Proxy**
```bash
cd proxy
./mvnw spring-boot:run
```
*El proxy iniciará en el puerto `8081`.*

---

## 🧪 Usuarios de Prueba

Para ingresar a la aplicación móvil se pueden utilizar las siguientes credenciales (generadas por defecto en el entorno de desarrollo):
- **Usuario:** user	**Contraseña:** user
- **Admin:** admin	**Contraseña:** admin
