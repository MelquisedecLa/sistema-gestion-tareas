# Proyecto - Aplicación de Gestión de Tareas y Recordatorios

Este repositorio contiene el desarrollo progresivo del proyecto de la materia, estructurado en tres entregables. Actualmente, el sistema se encuentra en la fase de lógica avanzada, patrones de diseño y simulación de procesos concurrentes por consola.

## Descripción del Proyecto

El proyecto consiste en una aplicación de gestión de tareas y recordatorios colaborativos dirigida a distintos tipos de usuarios registrados. El sistema implementa controles de membresía, flujos de procesamiento de pagos flexibles y mecanismos concurrentes en segundo plano.

## Características Principales (Entregable 2)

* **Modelado de Elementos:** Separación abstracta entre Tarea (con estados mutables) y Recordatorios (con reprogramación cronológica).
* **Control de Membresías:** Restricción de operaciones basada en el tipo de cuenta (`UsuarioClasico` con límite estricto de 3 elementos frente a `UsuarioPremium` con creación ilimitada).
* **Persistencia Temporal Concurrente:** Mecanismo de hilos de fondo que ejecuta un guardado automático del estado de las tareas sin interrumpir el flujo principal de la aplicación.

---

## Estructura de Entregables y Objetivos

### 1. Modelado y Estructura Inicial (Completado)
* **Objetivo:** Definición de la arquitectura base del sistema aplicando abstracción, herencia, encapsulamiento y polimorfismo. Ejecución con datos estáticos (quemados).

### 2. Comportamiento del Sistema y Concurrencia (Fase Actual)
* **Objetivo:** Incorporar comportamiento dinámico interactivo, patrones de diseño arquitectónicos y procesamiento multihilo.
* **Alcance:**
  * Aplicación del Patrón Strategy para desacoplar las pasarelas de pago del dominio de usuarios.
  * Implementación de un flujo interactivo de autenticación y captura de datos por consola.
  * Uso de la API de hilos de Java (`Thread`) mediante la clase `GestorTareasThread` para simular un daemon de autoguardado asíncrono.

### 3. Interfaz Gráfica y Persistencia (Siguiente Fase)
* **Objetivo:** Migración hacia un entorno visual GUI con persistencia relacional en SQL Server utilizando JavaFX (`ListView`, renderizado cromático por prioridad e iconografía).

---

## Patrones de Diseño e Hilos Aplicados

### Patrón Strategy (Estrategia)
Se utiliza para flexibilizar la gestión de pagos de los usuarios premium. La clase abstracta `Usuario` delega la responsabilidad a la interfaz `FormaPago`, permitiendo intercambiar dinámicamente el algoritmo de cobro en tiempo de ejecución:
* `TarjetaDebito`
* `TarjetaCredito`
* `Paypal`

### Concurrencia (Multithreading)
Al consolidar la creación de un elemento, el sistema realiza un **FORK** dividiendo el flujo de ejecución:
* **Hilo Principal (Main):** Continúa respondiendo al usuario e imprimiendo la información de la interfaz de consola.
* **Hilo Secundario (GestorTareasThread):** Corre de forma asíncrona en segundo plano, ejecutando una rutina de autoguardado cada 30 segundos antes de volver a unirse (**JOIN**) al terminar el ciclo de vida de la sesión.

---

## Guía de Uso del Proyecto en Java

Siga estos pasos para clonar, compilar y ejecutar la aplicación por consola:

1. **Clonar el repositorio:**
   ```bash
   git clone <URL_DEL_REPOSITORIO>
Configuración del Entorno:
Abra el proyecto en IntelliJ IDEA y asigne el SDK del proyecto a OpenJDK 17.0.12.

Construcción con Gradle:

Bash
./gradlew build
Ejecución de la Consola Interactiva:

Bash
./gradlew run
Indicaciones Específicas para Pruebas
Para validar los flujos modelados en los diagramas de secuencia y actividad, realice las pruebas bajo las siguientes directrices:

A. Formato de Datos Esperados
Credenciales de Acceso: Correo electrónico (usuario@dominio.com) y Clave de texto plano a través de la interfaz Autenticable.

Prioridad (Enum): Restringido estrictamente a: Alta, Media, Baja.

Estado de la Tarea (Enum): Administrado mediante transiciones válidas: PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA.

B. Casos de Prueba Críticos
Validación de Límite (Usuario Clásico): Intente registrar un 4.° elemento con una cuenta de tipo UsuarioClasico. El sistema invocará a verificarLimiteTareas() y bloqueará la creación disparando la alerta de "Límite de elementos alcanzado".

Comportamiento Asíncrono: Tras crear una tarea exitosamente, verifique la salida de la consola. El flujo principal le permitirá seguir navegando por los menús mientras, en intervalos de 30 segundos, el hilo secundario notificará la ejecución del demonio de autoguardado.

Ejemplos Prácticos de Entrada y Salida
Flujo de Ejecución Sincronizado en Consola
Entrada del Usuario:

Plaintext
=== SISTEMA DE GESTIÓN DE TAREAS ===
1. Iniciar Sesión / Registrarse
2. Salir
Seleccione una opción: 1

[LOGIN] Ingrese Email: carlos@mail.com
[LOGIN] Ingrese Contraseña: *******

>> Autenticando... ¡Bienvenido Carlos! [Tipo: Usuario Clasico]

--- MENÚ DE ACCIONES ---
1. Crear Nueva Tarea
2. Crear Recordatorio
3. Ver Elementos
Seleccione una opción: 1

[NUEVA TAREA] Ingrese Título: Diseñar diagramas UML
[NUEVA TAREA] Ingrese Descripción: Actualizar diagramas de secuencia y actividades.
[NUEVA TAREA] Seleccione Prioridad (Alta/Media/Baja): Alta
[NUEVA TAREA] Ingrese Fecha Límite (AAAA-MM-DD): 2026-06-01
Salida del Sistema:

Plaintext
>> [SISTEMA] Ejecutando verificarLimiteTareas()... Estado: Permito (1/3 elementos activos).
>> [SISTEMA] Tarea instanciada con éxito.

>> ---------- FORK DE PROCESOS ----------
>> [HILO PRINCIPAL]: Desplegando info de la tarea...
   ID: TR-001 | Título: Diseñar diagramas UML | Prioridad: Alta | Estado: PENDIENTE

>> [GUEST_THREAD - GestorTareasThread]: Hilo secundario activado.
   >> [INFO]: Guardado automático en segundo plano inicializado...
   >> [ÉXITO]: Progreso de la tarea respaldado de forma segura (Ciclo: cada 30s).
>> ---------------------------------------
