import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;
import sistemagestiontareas.model.ValidadorCorreo;
import sistemagestiontareas.thread.AutoGuardarThread;

// Strategy
import sistemagestiontareas.patterns.FormaPago;
import sistemagestiontareas.patterns.TarjetaCredito;
import sistemagestiontareas.patterns.TarjetaDebito;
import sistemagestiontareas.patterns.Paypal;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static List<Usuario> usuarios = new ArrayList<>();
    static Usuario usuarioActual = null;
    static ValidadorCorreo validador = new ValidadorCorreo();

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  SISTEMA DE GESTION DE TAREAS COLABORATIVAS");
        System.out.println("===========================================");

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerInt();
            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> iniciarSesion();
                case 3 -> { if (verificarSesion()) menuUsuario(); }
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    // ─── Menús ───────────────────────────────────────────────────────────────

    static void mostrarMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Iniciar sesion");
        System.out.println("3. Ir al menu de usuario");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    static void menuUsuario() {
        int opcion;
        do {
            System.out.println("\n--- MENU USUARIO: " + usuarioActual.getNombre() + " ---");
            System.out.println("1. Crear tarea");
            System.out.println("2. Crear recordatorio");
            System.out.println("3. Ver mis elementos");
            System.out.println("4. Compartir elemento");
            System.out.println("5. Cambiar estado de tarea");
            System.out.println("6. Cerrar sesion");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            opcion = leerInt();
            switch (opcion) {
                case 1 -> crearTarea();
                case 2 -> crearRecordatorio();
                case 3 -> usuarioActual.mostrarElementos();
                case 4 -> compartirElemento();
                case 5 -> cambiarEstadoTarea();
                case 6 -> {
                    usuarioActual.cerrarSesion();
                    usuarioActual = null;
                    opcion = 0;
                }
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    // ─── Funcionalidades ─────────────────────────────────────────────────────

    static void registrarUsuario() {
        System.out.println("\n-- Registro de usuario --");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        while (nombre.trim().isEmpty()) {
            System.out.print("El nombre no puede estar vacio.\n Nombre: ");
            nombre = scanner.nextLine();
        }

        String email = "";
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (!validador.validarFormato(email) || !validador.validarDominio(email)) {
                System.out.println("Correo invalido. Debe contener '@' y un dominio valido.");
                continue;
            }
            if (correoYaRegistrado(email)) {
                System.out.println("Este correo ya esta registrado. Usa uno diferente.");
                continue;
            }
            break;
        }

        System.out.print("Password (minimo 8 caracteres): ");
        String password = scanner.nextLine();
        while (password.length() < 8) {
            System.out.print("Debe tener al menos 8 caracteres: ");
            password = scanner.nextLine();
        }

        System.out.println("Tipo de usuario:");
        System.out.println("1. Clasico (max 3 elementos)");
        System.out.println("2. Premium (sin limite)");
        System.out.print("Opcion: ");
        int tipo = leerInt();

        int id = usuarios.size() + 1;

        try {
            if (tipo == 1) {
                Usuario nuevo = new UsuarioClasico(nombre, id, email, password);
                usuarios.add(nuevo);
                System.out.println("Usuario clasico registrado correctamente.");

            } else {
                System.out.println("Metodo de pago:");
                System.out.println("1. Tarjeta de credito");
                System.out.println("2. Tarjeta de debito");
                System.out.println("3. PayPal");
                System.out.print("Opcion: ");
                int opcionPago = leerInt();

                FormaPago metodoPago = null;

                if (opcionPago == 3) {
                    System.out.print("Correo de PayPal: ");
                    String correoPaypal = scanner.nextLine();
                    while (!validador.validarFormato(correoPaypal) || !validador.validarDominio(correoPaypal)) {
                        System.out.println("Correo de PayPal invalido.");
                        System.out.print("Correo de PayPal: ");
                        correoPaypal = scanner.nextLine();
                    }
                    System.out.print("Contrasena de PayPal: ");
                    String contrasenaPaypal = scanner.nextLine();
                    metodoPago = new Paypal(correoPaypal, contrasenaPaypal);

                } else {
                    String numeroTarjeta = "";
                    while (true) {
                        System.out.print("Numero de tarjeta (16 digitos): ");
                        numeroTarjeta = scanner.nextLine().replaceAll("\\s+", "");
                        if (numeroTarjeta.matches("\\d{16}")) break;
                        System.out.println("Numero invalido. Debe tener exactamente 16 digitos.");
                    }

                    LocalDate fechaVencimientoTarjeta = null;
                    while (true) {
                        System.out.print("Fecha de vencimiento (MM/YY): ");
                        String fechaStr = scanner.nextLine().trim();
                        if (fechaStr.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                            int mes = Integer.parseInt(fechaStr.split("/")[0]);
                            int anio = 2000 + Integer.parseInt(fechaStr.split("/")[1]);
                            fechaVencimientoTarjeta = LocalDate.of(anio, mes, 1);
                            if (fechaVencimientoTarjeta.isBefore(LocalDate.now())) {
                                System.out.println("La tarjeta esta vencida.");
                                continue;
                            }
                            break;
                        }
                        System.out.println("Formato invalido. Usa MM/YY, ejemplo: 08/27");
                    }

                    System.out.print("Nombre del titular: ");
                    String titular = scanner.nextLine();

                    int cvv = 0;
                    while (true) {
                        System.out.print("CVV (3 digitos): ");
                        String cvvStr = scanner.nextLine().trim();
                        if (cvvStr.matches("\\d{3}")) {
                            cvv = Integer.parseInt(cvvStr);
                            break;
                        }
                        System.out.println("CVV invalido. Debe tener exactamente 3 digitos.");
                    }

                    if (opcionPago == 1) {
                        metodoPago = new TarjetaCredito(numeroTarjeta, fechaVencimientoTarjeta, titular, cvv);
                        System.out.println("Tarjeta de credito registrada.");
                    } else {
                        metodoPago = new TarjetaDebito(numeroTarjeta, fechaVencimientoTarjeta, titular, cvv);
                        System.out.println("Tarjeta de debito registrada.");
                    }
                }

                LocalDate fechaExpiracion = LocalDate.now().plusMonths(1);
                Usuario nuevo = new UsuarioPremium(nombre, id, email, password, fechaExpiracion);
                nuevo.setMetodoPago(metodoPago);
                usuarios.add(nuevo);
                System.out.println("Usuario premium registrado correctamente.");
                System.out.println("Membresia valida hasta: " + fechaExpiracion);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static boolean correoYaRegistrado(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    static void iniciarSesion() {
        System.out.println("\n-- Inicio de sesion --");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (Usuario u : usuarios) {
            if (u.iniciarSesion(email, password)) {
                usuarioActual = u;
                System.out.println("Bienvenido, " + u.getNombre() + "!");

                if (u instanceof UsuarioPremium premium) {
                    boolean activa = premium.verificarMembresia();
                    System.out.println("Tipo: PREMIUM | Membresia: " + (activa ? "ACTIVA" : "VENCIDA"));
                    System.out.println("Vence: " + premium.getFechaExpiracion());
                } else {
                    System.out.println("Tipo: CLASICO");
                }
                return;
            }
        }
        System.out.println("Credenciales incorrectas.");
    }

    static void crearTarea() {
        System.out.println("\n-- Crear tarea --");

        System.out.print("Titulo: ");
        String titulo = scanner.nextLine();
        while (titulo.trim().isEmpty()) {
            System.out.print("El titulo no puede estar vacio: ");
            titulo = scanner.nextLine();
        }

        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine();

        Prioridad prioridad = leerPrioridad();
        LocalDate fechaLimite = leerFecha("Fecha limite");

        int id = usuarioActual.getElementos().size() + 1;

        // Crear la tarea ANTES del hilo para que el hilo tenga acceso a sus datos
        Tarea tarea = new Tarea(id, titulo, descripcion, prioridad, Estado.PENDIENTE, fechaLimite);

        // FORK: AutoGuardarThread corre en paralelo mientras el usuario llena los datos
        AutoGuardarThread autoSave = new AutoGuardarThread("Tarea", tarea);
        autoSave.start();

        // Verificar auto-guardado durante la entrada de datos
        if (autoSave.hayMensajePendiente()) {
            autoSave.mostrarAutoGuardado();
            // No pedir datos aquí, solo mostrar el mensaje
        }

        // Guardar la tarea
        usuarioActual.crearElemento(tarea);

        // JOIN: se detiene el hilo antes de guardar el elemento
        autoSave.detener();

        // Mostrar la tarea completa al final
        System.out.println("\n Tarea creada exitosamente!");
        System.out.println("═══════════════════════════════════════");
        tarea.mostrarInfo(); // Usar el método mostrarInfo() de Elemento
        System.out.println("═══════════════════════════════════════");
    }

    static void crearRecordatorio() {
        System.out.println("\n-- Crear recordatorio --");

        System.out.print("Titulo: ");
        String titulo = scanner.nextLine();

        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine();

        Prioridad prioridad = leerPrioridad();
        LocalDate fechaLimite = leerFecha("Fecha limite del recordatorio");

        int id = usuarioActual.getElementos().size() + 1;

        // Crear el recordatorio ANTES del hilo
        Recordatorio recordatorio = new Recordatorio(id, titulo, descripcion, prioridad, fechaLimite);

        // Iniciar hilo de auto-guardado con el recordatorio
        AutoGuardarThread autoSave = new AutoGuardarThread("Recordatorio", recordatorio);
        autoSave.start();

        // Verificar auto-guardado
        if (autoSave.hayMensajePendiente()) {
            autoSave.mostrarAutoGuardado();
        }

        // Guardar el recordatorio
        usuarioActual.crearElemento(recordatorio);

        // Detener el hilo
        autoSave.detener();

        // Mostrar el recordatorio completo al final
        System.out.println("\n✅ Recordatorio creado exitosamente!");
        System.out.println("═══════════════════════════════════════");
        recordatorio.mostrarInfo();
        System.out.println("═══════════════════════════════════════");
    }

    static void compartirElemento() {
        List<Elemento> elementos = usuarioActual.getElementos();
        if (elementos.isEmpty()) {
            System.out.println("No tienes elementos para compartir.");
            return;
        }

        System.out.println("\n-- Compartir elemento --");
        for (int i = 0; i < elementos.size(); i++)
            System.out.println((i + 1) + ". " + elementos.get(i).getTitulo());
        System.out.print("Selecciona elemento: ");
        int idxElemento = leerInt() - 1;

        List<Usuario> otros = new ArrayList<>();
        for (Usuario u : usuarios)
            if (!u.equals(usuarioActual)) otros.add(u);

        if (otros.isEmpty()) {
            System.out.println("No hay otros usuarios registrados.");
            return;
        }

        System.out.println("Usuarios disponibles:");
        for (int i = 0; i < otros.size(); i++)
            System.out.println((i + 1) + ". " + otros.get(i).getNombre());
        System.out.print("Selecciona usuario: ");
        int idxUsuario = leerInt() - 1;

        if (idxElemento >= 0 && idxElemento < elementos.size()
                && idxUsuario >= 0 && idxUsuario < otros.size()) {
            Elemento elementoACompartir = elementos.get(idxElemento);
            Usuario usuarioDestino = otros.get(idxUsuario);
            usuarioActual.compartirElemento(elementoACompartir, usuarioDestino);
            elementoACompartir.getUsuariosCompartidos().add(usuarioActual);
            usuarioDestino.crearElemento(elementoACompartir);
            System.out.println("Elemento agregado a la lista de " + usuarioDestino.getNombre());
        } else {
            System.out.println("Seleccion invalida.");
        }
    }

    static void cambiarEstadoTarea() {
        List<Tarea> tareas = new ArrayList<>();
        for (Elemento e : usuarioActual.getElementos())
            if (e instanceof Tarea) tareas.add((Tarea) e);

        if (tareas.isEmpty()) {
            System.out.println("No tienes tareas.");
            return;
        }

        System.out.println("\n-- Cambiar estado de tarea --");
        for (int i = 0; i < tareas.size(); i++)
            System.out.println((i + 1) + ". " + tareas.get(i).getTitulo()
                    + " [" + tareas.get(i).getEstado() + "]");
        System.out.print("Selecciona tarea: ");
        int idx = leerInt() - 1;

        System.out.println("1. PENDIENTE  2. EN_PROGRESO  3. COMPLETADA  4. CANCELADA");
        System.out.print("Opcion: ");

        Estado nuevoEstado = switch (leerInt()) {
            case 1 -> Estado.PENDIENTE;
            case 2 -> Estado.EN_PROGRESO;
            case 3 -> Estado.COMPLETADA;
            case 4 -> Estado.CANCELADA;
            default -> null;
        };

        if (nuevoEstado != null && idx >= 0 && idx < tareas.size()) {
            tareas.get(idx).cambiarEstado(nuevoEstado);
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    // --- Helpers --------

    static boolean verificarSesion() {
        if (usuarioActual == null) {
            System.out.println("Debes iniciar sesion primero.");
            return false;
        }
        return true;
    }

    static int leerInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static Prioridad leerPrioridad() {
        System.out.println("Prioridad: 1. ALTA  2. MEDIA  3. BAJA");
        System.out.print("Opcion: ");
        return switch (leerInt()) {
            case 1 -> Prioridad.ALTA;
            case 2 -> Prioridad.MEDIA;
            default -> Prioridad.BAJA;
        };
    }

    static LocalDate leerFecha(String etiqueta) {
        while (true) {
            System.out.print(etiqueta + " (YYYY-MM-DD): ");
            try {
                LocalDate fecha = LocalDate.parse(scanner.nextLine());
                if (fecha.isBefore(LocalDate.now())) {
                    System.out.println("La fecha no puede ser en el pasado.");
                    continue;
                }
                return fecha;
            } catch (DateTimeParseException e) {
                System.out.println("Formato invalido. Usa YYYY-MM-DD.");
            }
        }
    }
}