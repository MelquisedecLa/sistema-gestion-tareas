import java.time.LocalDate;
import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;

public class Main {

    public static void main(String[] args) {
        System.out.println("SISTEMA DE GESTION DE TAREAS COLABORATIVAS");
        System.out.println("PRIMER ENTREGABLE");

        System.out.println("\nCREACION DE USUARIOS");

        UsuarioClasico usuarioClasico =
                new UsuarioClasico("Luis", 1, "00122425@uca.edu.sv", "xd123");

        UsuarioPremium usuarioPremium =
                new UsuarioPremium("Melquisedec", 2, "00029425@uca.edu.sv", "uca123");

        System.out.println("Usuario clasico: " + usuarioClasico.getNombre());
        System.out.println("Usuario premium: " + usuarioPremium.getNombre());

        System.out.println("\nINICIO DE SESION");

        if (usuarioClasico.iniciarSesion("00122425@uca.edu.sv", "xd123")) {
            System.out.println("Inicio de sesion correcto para: " + usuarioClasico.getNombre());
        }

        if (usuarioPremium.iniciarSesion("00029425@uca.edu.sv", "uca123")) {
            System.out.println("Inicio de sesion correcto para: " + usuarioPremium.getNombre());
        }

        System.out.println("\nCREACION DE ELEMENTOS");

        Tarea tarea =
                new Tarea(
                        101,
                        "Estudiar POO",
                        "Repasar herencia, interfaces y polimorfismo",
                        Prioridad.ALTA,
                        Estado.PENDIENTE);

        Recordatorio recordatorio =
                new Recordatorio(
                        201,
                        "Entrega del proyecto",
                        "Subir el primer entregable al repositorio",
                        Prioridad.MEDIA,
                        LocalDate.of(2026, 4, 14));

        usuarioClasico.crearElemento(tarea);
        usuarioPremium.crearElemento(recordatorio);

        System.out.println("\nELEMENTOS CREADOS POR CADA USUARIO");
        usuarioClasico.mostrarElementos();
        usuarioPremium.mostrarElementos();

        System.out.println("\nCAMBIO DE ESTADO DE TAREA");
        tarea.cambiarEstado(Estado.EN_PROGRESO);
        tarea.mostrarInfo();

        System.out.println("\nREPROGRAMACION DE RECORDATORIO");
        recordatorio.reprogramarFecha(LocalDate.of(2026, 4, 16));
        recordatorio.mostrarInfo();

        System.out.println("\nCOMPARTIR ELEMENTOS");
        tarea.compartir(usuarioPremium);
        recordatorio.compartir(usuarioClasico);

        System.out.println("\nPOLIMORFISMO");

        Usuario usuario1 = usuarioClasico;
        Usuario usuario2 = usuarioPremium;

        System.out.println("Usuario 1: " + usuario1.getNombre());
        System.out.println("Usuario 2: " + usuario2.getNombre());

        usuario1.cerrarSesion();
        usuario2.cerrarSesion();

        System.out.println("\nPROGRAMA FINALIZADO");
    }
}