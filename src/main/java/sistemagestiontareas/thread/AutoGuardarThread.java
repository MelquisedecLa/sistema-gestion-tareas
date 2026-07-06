package sistemagestiontareas.thread;

import sistemagestiontareas.model.Elemento;

public class AutoGuardarThread extends Thread {
    private boolean ejecutando = true;
    private String tipoElemento;
    private Elemento elementoEnCreacion;
    private int segundos = 0;
    private boolean mensajePendiente = false;

    public AutoGuardarThread(String tipoElemento, Elemento elemento) {
        this.tipoElemento = tipoElemento;
        this.elementoEnCreacion = elemento;
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                Thread.sleep(1000);
                segundos++;

                if (segundos % 30 == 0 && !mensajePendiente) {
                    mensajePendiente = true;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public boolean hayMensajePendiente() {
        if (mensajePendiente) {
            mensajePendiente = false;
            return true;
        }
        return false;
    }

    public void mostrarAutoGuardado() {
        System.out.println("\n [Auto-Guardado] " + tipoElemento + " guardada automáticamente...");
        if (elementoEnCreacion != null) {
            System.out.println("    Información actual del elemento:");
            System.out.println("   ──────────────────────────────────────");
            System.out.println("   ID: " + elementoEnCreacion.getId());
            System.out.println("   Título: " + elementoEnCreacion.getTitulo());
            System.out.println("   Descripción: " + elementoEnCreacion.getDescripcion());
            System.out.println("   Prioridad: " + elementoEnCreacion.getPrioridad());
            System.out.println("   Fecha límite: " + elementoEnCreacion.getFechaLimite());
            System.out.println("   ──────────────────────────────────────");
        }
        System.out.print("Continuando con la creación... ");
    }

    public void detener() {
        ejecutando = false;
        this.interrupt();
    }
}