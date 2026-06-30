package sistemagestiontareas.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;

import java.util.function.Consumer;

public class ElementoListCell extends ListCell<Elemento> {

    private final Consumer<Elemento> onEliminar;
    private final Consumer<Tarea> onCambiarEstado;
    private final Consumer<Recordatorio> onReprogramar;
    private final Consumer<Elemento> onEditar;

    public ElementoListCell(Consumer<Elemento> onEliminar,
                            Consumer<Tarea> onCambiarEstado,
                            Consumer<Recordatorio> onReprogramar,
                            Consumer<Elemento> onEditar) {
        this.onEliminar = onEliminar;
        this.onCambiarEstado = onCambiarEstado;
        this.onReprogramar = onReprogramar;
        this.onEditar = onEditar;

        selectedProperty().addListener((obs, antes, ahora) -> updateItem(getItem(), isEmpty()));
    }

    @Override
    protected void updateItem(Elemento elemento, boolean vacio) {
        super.updateItem(elemento, vacio);

        if (vacio || elemento == null) {
            setText(null);
            setGraphic(null);
            setStyle("");
            return;
        }

        boolean esRecordatorio = elemento instanceof Recordatorio;

        Label icono = new Label(esRecordatorio ? "[R]" : "[T]");
        icono.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-min-width: 28;");

        Label titulo = new Label(elemento.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f2937;");

        String detalle = elemento.getDescripcion();
        if (elemento instanceof Tarea tarea) {
            detalle += "  [" + tarea.getEstado() + "]";
        }
        Label info = new Label(detalle);
        info.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 11px;");

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        HBox contenedor = new HBox(10, icono, titulo, info, espaciador);
        contenedor.setStyle("-fx-alignment: center-left; -fx-padding: 8;");

        if (elemento instanceof Tarea tarea) {
            Button btnEstado = new Button("Cambiar estado");
            btnEstado.setStyle(botonSecundario());
            btnEstado.setOnAction(e -> { if (onCambiarEstado != null) onCambiarEstado.accept(tarea); });
            contenedor.getChildren().add(btnEstado);
        } else if (elemento instanceof Recordatorio recordatorio) {
            Button btnReprogramar = new Button("Reprogramar");
            btnReprogramar.setStyle(botonSecundario());
            btnReprogramar.setOnAction(e -> { if (onReprogramar != null) onReprogramar.accept(recordatorio); });
            contenedor.getChildren().add(btnReprogramar);
        }

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 10; -fx-background-radius: 4;");
        btnEditar.setOnAction(e -> { if (onEditar != null) onEditar.accept(elemento); });
        contenedor.getChildren().add(btnEditar);

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 10; -fx-background-radius: 4;");
        btnEliminar.setOnAction(e -> { if (onEliminar != null) onEliminar.accept(elemento); });
        contenedor.getChildren().add(btnEliminar);

        setGraphic(contenedor);
        setText(null);

        String colorFondo = colorPorPrioridad(elemento.getPrioridad());
        String borde = isSelected()
                ? "-fx-border-color: #2563eb; -fx-border-width: 2; -fx-border-radius: 4;"
                : "-fx-border-width: 0;";
        setStyle(colorFondo + borde);
    }

    private String botonSecundario() {
        return "-fx-background-color: #374151; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 10; -fx-background-radius: 4;";
    }

    private String colorPorPrioridad(Prioridad prioridad) {
        return switch (prioridad) {
            case ALTA -> "-fx-background-color: #fecaca;";
            case MEDIA -> "-fx-background-color: #fef3c7;";
            case BAJA -> "-fx-background-color: #bbf7d0;";
        };
    }
}