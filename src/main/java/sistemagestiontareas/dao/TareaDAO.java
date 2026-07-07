package sistemagestiontareas.dao;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.model.Tarea;
import java.util.List;

public interface TareaDAO {

    int guardar(Tarea tarea, int usuarioId);
    Tarea buscarPorId(int id);
    List<Tarea> buscarPorUsuario(int usuarioId);
    List<Tarea> buscarTodos();
    boolean actualizar(Tarea tarea);
    boolean actualizarEstado(int id, Estado estado);
    boolean eliminar(int id);
}