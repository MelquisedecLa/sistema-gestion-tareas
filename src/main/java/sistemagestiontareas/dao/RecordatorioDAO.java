package sistemagestiontareas.dao;

import sistemagestiontareas.model.Recordatorio;
import java.util.List;

public interface RecordatorioDAO {

    int guardar(Recordatorio recordatorio, int usuarioId);
    Recordatorio buscarPorId(int id);
    List<Recordatorio> buscarPorUsuario(int usuarioId);
    List<Recordatorio> buscarTodos();
    boolean actualizar(Recordatorio recordatorio);
    boolean eliminar(int id);
}