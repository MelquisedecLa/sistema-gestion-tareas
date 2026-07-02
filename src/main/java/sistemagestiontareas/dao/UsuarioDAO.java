package sistemagestiontareas.dao;

import sistemagestiontareas.model.Usuario;
import java.util.List;

public interface UsuarioDAO {

    // Guarda el usuario y su forma de pago si es premium, devuelve el id generado
    int guardar(Usuario usuario);

    Usuario buscarPorId(int id);

    // Busca por email para el login, reconstruye Clasico o Premium segun el tipo
    Usuario buscarPorEmail(String email);

    List<Usuario> buscarTodos();

    boolean actualizar(Usuario usuario);

    boolean eliminar(int id);

    // Para no dejar registrar dos veces el mismo correo
    boolean existeEmail(String email);
}