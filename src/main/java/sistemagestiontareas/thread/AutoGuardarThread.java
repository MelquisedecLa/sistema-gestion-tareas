package sistemagestiontareas.thread;

public class AutoGuardarThread extends Thread {
    private boolean ejecutando = true;
    private String tipoElemento;
    private int segundos = 0;

    public AutoGuardarThread(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                Thread.sleep(1000);
                segundos++;

                if (segundos % 30 == 0) {

                    System.out.print("\r");
                    System.out.print(" [Auto-Guardado] " + tipoElemento + " guardado automáticamente...");
                    // aqui falta autoguardar
                    System.out.print("\n\r");
                    System.out.print("Puedes continuar editando el campo anterior: ");
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void detener() {
        ejecutando = false;
        this.interrupt();
    }
}