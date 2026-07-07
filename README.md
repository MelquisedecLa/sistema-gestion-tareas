# Proyecto - Aplicación de Gestión de Tareas y Recordatorios

Este repositorio contiene el desarrollo progresivo del proyecto de la materia, estructurado en tres entregables. El sistema se encuentra en su **fase final**, con interfaz gráfica completa, persistencia en base de datos relacional y concurrencia integrada.

---

## Descripción del Proyecto

El proyecto consiste en una **aplicación de gestión de tareas y recordatorios colaborativos** dirigida a distintos tipos de usuarios registrados. El sistema implementa controles de membresía, flujos de procesamiento de pagos flexibles y mecanismos concurrentes en segundo plano, con una interfaz gráfica desarrollada en JavaFX y persistencia en PostgreSQL.

Los usuarios pueden **crear, organizar y compartir tareas y recordatorios**, los cuales se visualizan en un `ListView` con información básica de cada elemento:

- Las **tareas** se diferencian mediante **colores de fondo según su prioridad**: rojo (ALTA), naranja (MEDIA) y verde (BAJA).
- Los **recordatorios** se identifican con el prefijo **[R]** y las tareas con **[T]** dentro de la lista.

---

## Características Principales (Entregable 3)

- **Interfaz Gráfica con JavaFX:** Pantallas de login, registro y panel principal desarrolladas con FXML y controladores. Navegación entre escenas gestionada por la clase `App`.
- **Persistencia en PostgreSQL:** Todas las operaciones CRUD (crear, leer, actualizar, eliminar) sobre usuarios, tareas y recordatorios se persisten en base de datos relacional mediante la capa DAO (`TareaDAOImpl`, `RecordatorioDAOImpl`, `UsuarioDAOImpl`, `ElementoCompartidoDAOImpl`).
- **Visualización en ListView con colores por prioridad:** Las tareas y recordatorios se muestran en una lista con celdas personalizadas (`ElementoListCell`). El color de fondo indica la prioridad: rojo (ALTA), naranja (MEDIA) y verde (BAJA).
- **Fecha y hora en recordatorios:** Los recordatorios almacenan fecha y hora límite (`LocalDateTime`), permitiendo programar recordatorios con precisión horaria.
- **Compartir elementos entre usuarios:** Un usuario puede compartir cualquier tarea o recordatorio con otro usuario registrado, respetando el límite de elementos del destinatario.
- **Control de membresía en tiempo real:** El sistema valida el límite de 3 elementos para `UsuarioClasico` tanto al crear como al recibir elementos compartidos.
- **Concurrencia FORK/JOIN:** Al abrir el formulario de creación, `AutoGuardarThread` se activa en segundo plano (FORK) y se detiene al cerrar el formulario (JOIN).
- **Documentación Javadoc:** Todas las clases activas del sistema están documentadas con el estándar Javadoc de Java.

---

## Tecnologías Utilizadas

- **Java 17** con principios de Programación Orientada a Objetos (POO)
- **JavaFX 21** para la interfaz gráfica (FXML + controladores)
- **PostgreSQL** para la persistencia de datos
- **Gradle** como herramienta de construcción y gestión de dependencias
- **Programación multihilo** (patrón FORK/JOIN con `AutoGuardarThread`)
- **Patrón Strategy** para el procesamiento de pagos

---

## Estructura de Entregables

### 1. Modelado y Estructura Inicial ✅ Completado

**Objetivo:** Definición de la arquitectura base del sistema.

- Diseño aplicando abstracción, herencia, encapsulamiento y polimorfismo
- Diagrama de clases y diagrama de objetos
- Uso de clases abstractas e interfaces
- Implementación inicial en Java con Gradle
- Ejecución con datos estáticos en consola (sin interfaz gráfica)

---

### 2. Comportamiento del Sistema y Concurrencia ✅ Completado

**Objetivo:** Incorporar comportamiento dinámico, patrones de diseño y procesamiento multihilo.

- **Patrón Strategy** para desacoplar las pasarelas de pago (`TarjetaCredito`, `TarjetaDebito`, `Paypal`)
- **Hilos (Threads):** clase `AutoGuardarThread` que simula un daemon de autoguardado asíncrono
- Flujo interactivo de autenticación y captura de datos por consola
- Diagramas de secuencia y actividad
- Aplicación funcionando en consola

---

### 3. Interfaz Gráfica y Persistencia ✅ Fase Actual

**Objetivo:** Migración hacia entorno visual GUI con persistencia relacional en PostgreSQL.

- Interfaz gráfica completa con **JavaFX** (login, registro, pantalla principal)
- Conexión a **PostgreSQL** para gestionar usuarios, tareas y recordatorios
- Visualización de elementos en `ListView` con colores por prioridad
- Diferenciación visual entre tareas `[T]` y recordatorios `[R]`
- Compartir elementos entre usuarios registrados
- Control de límite de elementos para `UsuarioClasico` (máximo 3)
- Documentación Javadoc generada para todas las clases activas

---

## Patrones de Diseño e Hilos Aplicados

### Patrón Strategy

La clase abstracta `Usuario` delega la responsabilidad del pago a la interfaz `FormaPago`, permitiendo intercambiar el método de cobro en tiempo de ejecución:

- `TarjetaDebito`
- `TarjetaCredito`
- `Paypal`

### Concurrencia (FORK/JOIN con AutoGuardarThread)

Al abrir el formulario de creación de una tarea o recordatorio, el sistema realiza un **FORK** dividiendo el flujo de ejecución:

- **Hilo Principal:** muestra el formulario modal y espera la interacción del usuario.
- **Hilo Secundario (`AutoGuardarThread`):** corre de forma asíncrona en segundo plano, ejecutando una rutina de autoguardado cada 5 segundos.
- Al cerrar el formulario se realiza el **JOIN**: el hilo secundario recibe la señal de parada y termina de forma controlada.

---

## Requisitos Previos

1. **Java 17** instalado (OpenJDK 17 o superior)
2. **PostgreSQL** instalado y en ejecución
3. **Gradle** (incluido via wrapper `gradlew`)

### Configuración de la Base de Datos

Crear la base de datos y las tablas ejecutando el script SQL del repositorio en PostgreSQL:

```
Base de datos : ProyectoPOO
Usuario       : postgres
Contraseña    : Luis0729
Puerto        : 5432 (por defecto)
```

---

## Guía de Uso del Proyecto

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

### 2. Configurar el entorno

Abrir el proyecto en VS Code o IntelliJ IDEA con **Java 17** como SDK del proyecto.

### 3. Construir el proyecto

```bash
./gradlew build
```

En Windows (PowerShell):

```powershell
.\gradlew.bat build
```

### 4. Ejecutar la aplicación

```bash
./gradlew run
```

En Windows (PowerShell):

```powershell
.\gradlew.bat run
```

Esto abrirá la ventana de **inicio de sesión** de la aplicación.

---

## Indicaciones Específicas para Pruebas

### A. Registro de usuario

1. En la pantalla de login, hacer clic en **"Registrarse"**
2. Completar los campos:
   - **Nombre:** texto libre (ej. `Carlos López`)
   - **Correo:** formato `usuario@dominio.com` (ej. `carlos@mail.com`)
   - **Contraseña:** texto libre (mínimo recomendado: 6 caracteres)
   - **Tipo de cuenta:** `Clásico` (límite de 3 elementos) o `Premium`
   - Si elige **Premium**, debe seleccionar un método de pago y completar los datos correspondientes
3. Hacer clic en **"Registrar"**

### B. Inicio de sesión

1. Ingresar el correo y contraseña del usuario registrado
2. Hacer clic en **"Iniciar sesión"**
3. Si los datos son correctos, se accede a la pantalla principal con la lista de elementos

### C. Crear una tarea

1. En la pantalla principal, hacer clic en **"Nueva Tarea"**
2. Completar el formulario:
   - **Título:** obligatorio (ej. `Diseñar diagramas UML`)
   - **Descripción:** opcional (ej. `Actualizar diagramas de secuencia`)
   - **Prioridad:** seleccionar entre `ALTA`, `MEDIA` o `BAJA`
   - **Fecha límite:** seleccionar una fecha futura en el calendario
3. Hacer clic en **"Guardar"**

> La tarea aparecerá en la lista con el prefijo **[T]** y el color de fondo según su prioridad.

### D. Crear un recordatorio

1. Hacer clic en **"Nuevo Recordatorio"**
2. Completar el formulario:
   - **Título:** obligatorio
   - **Descripción:** opcional
   - **Prioridad:** seleccionar entre `ALTA`, `MEDIA` o `BAJA`
   - **Fecha límite:** seleccionar una fecha futura en el calendario
   - **Hora (HH:mm):** ingresar la hora en formato de 24 horas (ej. `14:30`). Por defecto se carga la hora actual.
3. Hacer clic en **"Guardar Recordatorio"**

> El recordatorio aparecerá en la lista con el prefijo **[R]**.

### E. Editar o eliminar un elemento

- Cada elemento en la lista tiene botones **"Editar"** y **"Eliminar"** integrados.
- Para las tareas, también aparece el botón **"Cambiar estado"** (PENDIENTE → EN_PROGRESO → COMPLETADA → CANCELADA).
- Para los recordatorios, aparece el botón **"Reprogramar"** para cambiar la fecha y hora.

### F. Compartir un elemento

1. Seleccionar un elemento de la lista haciendo clic sobre él
2. Hacer clic en el botón **"Compartir"**
3. Seleccionar el usuario destino del listado
4. Confirmar

> Si el usuario destino es `UsuarioClasico` y ya alcanzó su límite de 3 elementos, el sistema bloqueará el compartir y mostrará una advertencia.

### G. Validación del límite (UsuarioClasico)

Intentar crear un 4.° elemento con una cuenta de tipo `UsuarioClasico`. El sistema bloqueará la operación y mostrará: *"Alcanzaste el límite de elementos permitidos para tu cuenta Clásica."*

---

## Ejemplos Prácticos

### Flujo de creación de tarea (UsuarioClasico)

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Login con `carlos@mail.com` | Pantalla principal: "Hola, Carlos" / "Clásico" |
| 2 | Clic en "Nueva Tarea" | Se abre formulario modal + `AutoGuardarThread` activo en segundo plano |
| 3 | Título: `Revisar código`, Prioridad: `ALTA`, Fecha: futura | Formulario válido |
| 4 | Clic en "Guardar" | Tarea guardada en BD, aparece en lista con fondo rojo y prefijo [T] |
| 5 | Intentar crear un 4.° elemento | Sistema bloquea y muestra advertencia de límite |

### Flujo de compartir elemento

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Seleccionar una tarea de la lista | Elemento resaltado |
| 2 | Clic en "Compartir" | Diálogo con lista de usuarios disponibles |
| 3 | Seleccionar usuario destino | Confirmación: "Compartido con [nombre] correctamente." |
| 4 | El usuario destino inicia sesión | El elemento aparece también en su lista |

---

## Generación de Documentación Javadoc

Para generar la documentación HTML del proyecto:

```powershell
.\gradlew.bat javadoc
```

La documentación se genera en `C:\javadoc-proyectopoo\index.html` y puede abrirse en cualquier navegador web.
