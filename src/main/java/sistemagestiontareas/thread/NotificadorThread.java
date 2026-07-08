package sistemagestiontareas.thread;

import sistemagestiontareas.dao.RecordatorioDAO;
import sistemagestiontareas.dao.RecordatorioDAOImpl;
import sistemagestiontareas.dao.TareaDAO;
import sistemagestiontareas.dao.TareaDAOImpl;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hilo que revisa periodicamente las tareas y recordatorios del usuario
 * y muestra una notificacion real de Windows cuando la fecha limite esta cerca.
 */
public class NotificadorThread extends Thread {

    private final int usuarioId;
    private volatile boolean ejecutando = true;
    private final Set<Integer> yaNotificados = new HashSet<>();
    private final TareaDAO tareaDAO = new TareaDAOImpl();
    private final RecordatorioDAO recordatorioDAO = new RecordatorioDAOImpl();
    private TrayIcon trayIcon;

    private static final int DIAS_AVISO = 2;
    private static final long INTERVALO_MS = 60_000;

    public NotificadorThread(int usuarioId) {
        super("Notificador");
        this.usuarioId = usuarioId;
        setDaemon(true);
        inicializarBandeja();
    }

    private void inicializarBandeja() {
        if (!SystemTray.isSupported()) return;
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image imagen = Toolkit.getDefaultToolkit().createImage(
                    getClass().getResource("/sistemagestiontareas/icons/app_icon.png"));
            trayIcon = new TrayIcon(imagen, "Sistema de Gestión de Tareas");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
        } catch (AWTException e) {
            trayIcon = null;
        }
    }

    @Override
    public void run() {
        while (ejecutando) {
            revisarPendientes();
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void revisarPendientes() {
        List<Tarea> tareas = tareaDAO.buscarPorUsuario(usuarioId);
        for (Tarea t : tareas) revisarElemento(t);

        List<Recordatorio> recordatorios = recordatorioDAO.buscarPorUsuario(usuarioId);
        for (Recordatorio r : recordatorios) revisarElemento(r);
    }

    private void revisarElemento(Elemento elemento) {
        if (yaNotificados.contains(elemento.getId())) return;

        LocalDate hoy = LocalDate.now();
        LocalDate limite = elemento.getFechaLimite();
        long diasRestantes = ChronoUnit.DAYS.between(hoy, limite);

        if (diasRestantes >= 0 && diasRestantes <= DIAS_AVISO) {
            notificar(elemento);
            yaNotificados.add(elemento.getId());
        }
    }

    private void notificar(Elemento elemento) {
        String titulo = (elemento instanceof Tarea) ? "Tarea próxima a vencer" : "Recordatorio próximo";
        String mensaje = elemento.getTitulo() + " vence el " + elemento.getFechaLimite();

        if (trayIcon != null) {
            trayIcon.displayMessage(titulo, mensaje, MessageType.INFO);
        } else {
            System.out.println("[Notificación] " + titulo + ": " + mensaje);
        }
    }

    public void detener() {
        ejecutando = false;
        this.interrupt();
    }
}