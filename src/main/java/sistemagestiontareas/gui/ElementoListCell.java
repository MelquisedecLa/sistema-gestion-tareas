package sistemagestiontareas.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;

/**
 * Celda personalizada para el {@link javafx.scene.control.ListView} de elementos
 * que se muestra en {@link DashboardView}.
 *
 * <p>Cumple los dos requisitos visuales del entregable:</p>
 * <ul>
 *   <li>Color de fondo diferenciado segun la {@link Prioridad} del elemento:
 *       rojo para {@code ALTA}, naranja para {@code MEDIA}, verde para {@code BAJA}.</li>
 *   <li>Icono emoji distinto segun el tipo concreto: 🔔 para {@link Recordatorio}
 *       y ✅ para {@link Tarea}, facilitando la lectura rapida de la lista.</li>
 * </ul>
 *
 * <p>La celda muestra adicionalmente la fecha limite, el estado (solo en tareas)
 * y una etiqueta "Compartido" si el elemento fue compartido con otros usuarios.</p>
 */
public class ElementoListCell extends ListCell<Elemento> {

    /**
     * Actualiza el contenido visual de la celda cada vez que el {@link javafx.scene.control.ListView}
     * la reutiliza para un nuevo elemento (mecanismo de virtualizacion de JavaFX).
     *
     * <p>Si la celda esta vacia o el elemento es {@code null}, limpia el grafico y
     * el estilo para evitar residuos visuales de ciclos anteriores.</p>
     *
     * @param elemento el elemento de dominio a representar; puede ser {@code null}
     * @param vacio    {@code true} si la celda no tiene un elemento asignado
     */
    @Override
    protected void updateItem(Elemento elemento, boolean vacio) {
        super.updateItem(elemento, vacio);

        if (vacio || elemento == null) {
            setText(null);
            setGraphic(null);
            setStyle("");
            return;
        }

        Label icono = new Label(elemento instanceof Recordatorio ? "🔔" : "✅");
        icono.setStyle("-fx-font-size: 18px;");

        Label titulo = new Label(elemento.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        StringBuilder detalle = new StringBuilder();
        detalle.append("Prioridad: ").append(elemento.getPrioridad())
                .append("   |   Vence: ").append(elemento.getFechaLimite());
        if (elemento instanceof Tarea tarea) {
            detalle.append("   |   Estado: ").append(tarea.getEstado());
        } else {
            detalle.append("   |   Recordatorio");
        }
        if (!elemento.getUsuariosCompartidos().isEmpty()) {
            detalle.append("   |   Compartido");
        }

        Label subtitulo = new Label(detalle.toString());
        subtitulo.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;");

        VBox textos = new VBox(2, titulo, subtitulo);
        HBox contenedor = new HBox(12, icono, textos);
        contenedor.setAlignment(Pos.CENTER_LEFT);

        setGraphic(contenedor);
        setText(null);
        setStyle(colorDeFondo(elemento.getPrioridad()));
    }

    /**
     * Retorna la cadena CSS de color de fondo correspondiente a la prioridad indicada.
     *
     * @param prioridad nivel de prioridad del elemento ({@link Prioridad#ALTA},
     *                  {@link Prioridad#MEDIA} o {@link Prioridad#BAJA})
     * @return cadena CSS {@code -fx-background-color: <color>;}
     */
    private String colorDeFondo(Prioridad prioridad) {
        return switch (prioridad) {
            case ALTA  -> "-fx-background-color: #ffcdd2;";  // rojo claro
            case MEDIA -> "-fx-background-color: #ffe0b2;";  // naranja claro
            case BAJA  -> "-fx-background-color: #c8e6c9;";  // verde claro
        };
    }
}
