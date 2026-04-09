# Proyecto - Aplicación de Gestión de Tareas y Recordatorios

Este repositorio está destinado a la entrega del proyecto de la materia, el cual se desarrollará progresivamente en **tres entregables**.

## Descripción del Proyecto

El proyecto consiste en desarrollar una **aplicación de gestión de tareas y recordatorios colaborativos** dirigida a distintos tipos de usuarios registrados, permitiendo realizar acciones según el tipo de usuario y ofreciendo una solución escalable.

Los usuarios podrán **crear, organizar y compartir tareas y recordatorios**, los cuales se visualizarán en una lista (`ListView`) mostrando información básica de cada elemento.

Para mejorar la visualización:

- Las **tareas** se diferenciarán mediante **colores según su prioridad**.
- Los **recordatorios** contarán con **íconos distintivos** que los separen de las tareas.

## Tecnologías y Enfoque de Desarrollo

El desarrollo del proyecto se realizará en **Java**, siguiendo los principios de **Programación Orientada a Objetos (POO)** y aplicando **patrones de diseño** para garantizar flexibilidad, mantenimiento y escalabilidad.

Además, se incorporará:

- **Programación concurrente o multihilo** para simular accesos simultáneos a tareas compartidas.
- **Persistencia de datos con SQL Server** para almacenamiento y recuperación eficiente de la información.
- **Gradle** como herramienta de gestión de dependencias y construcción del proyecto.
- **JavaFX** para la interfaz gráfica final.

## Estructura de Entregables

El proyecto se desarrollará progresivamente en **tres entregables**:

---

### 1. Modelado y Estructura Inicial

En esta fase se definirá la arquitectura base del sistema.

Incluye:

- Diseño del sistema aplicando **principios de POO**.
- **Diagrama de clases** con:
  - Herencia
  - Polimorfismo
  - Encapsulamiento
  - Abstracción
- Uso de **clases abstractas** e **interfaces**.
- **Diagrama de objetos**.
- Implementación inicial en **Java usando Gradle**.
- Ejecución del sistema **en consola**.
- **No incluye interfaz gráfica**.

---

### 2. Comportamiento del Sistema y Concurrencia

En esta fase se ampliará el diseño incorporando comportamiento avanzado del sistema.

Incluye:

- Extensión del diseño para incorporar **patrones de diseño** y **concurrencia**.
- **Actualización del diagrama de clases** y uso de patrones.
- **Diagramas de secuencia y actividad** para la gestión de tareas.
- Implementación en Java de:
  - **Patrones de diseño**
  - **Hilos (Threads)** para accesos concurrentes a tareas compartidas.
- La aplicación **continúa funcionando en consola**.

---

### 3. Interfaz Gráfica y Persistencia

Fase final del proyecto con interfaz visual y almacenamiento de datos.

Incluye:

- Implementación final con **interfaz gráfica y persistencia de datos**.
- Actualización del **diagrama de clases** si se agregan componentes de interfaz o persistencia.
- Conexión a **SQL Server** para gestionar:
  - Usuarios
  - Tareas
  - Recordatorios
- Desarrollo de interfaz con **JavaFX**.
- Visualización de tareas en **ListView**.
- **Colores según prioridad** para tareas.
- **Íconos para diferenciar recordatorios**.

- ## Guía de uso del proyecto en JAVA
- A continuación se explicara como hacer uso correcto del programa:

- 1 - Ingresar al link proporcionado mediante el envio del entregable 1.
- 2 - Copiar el link del repositorio y clonarlo a través de la terminal o por medio de Git.
- 3 - Al momento de abrir el proyecto en IntelliJ IDEA es necesario hacer uso del OpenJDK 17.0.12.
- 3 - Por medio del uso de graddle realizar la construcción del ejecutable del programa.
- 4 - Usar graddle run para ejecutar el archivo ejecutable del programa, esto permitira ver el funcionamiento.
- 5 - El programa no pide la entrada de datos al usuario, ya que el programa funciona usando datos predefinidos por los desarrolladores.
- 6 - Una vez terminado de usar el programa solo es necesario salir del proyecto.
