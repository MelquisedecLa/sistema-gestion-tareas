package sistemagestiontareas.dao;

import sistemagestiontareas.model.Usuario;
import java.util.List;

public interface ElementoCompartidoDAO {

    boolean compartir(int elementoId, int usuarioId);
    List<Usuario> buscarUsuariosCompartidos(int elementoId);
    List<Integer> buscarIdsCompartidosConUsuario(int usuarioId);
}