package sistemagestiontareas.dao;

import sistemagestiontareas.model.Usuario;

/**
 * Define las operaciones de persistencia disponibles para Usuario.
 */
public interface UsuarioDAO {

    /**
     * Inserta un nuevo usuario (y su forma de pago si aplica) en la base de datos.
     *
     * @param usuario usuario a guardar
     * @return el id generado por la base de datos
     */
    int guardar(Usuario usuario);

    /**
     * Busca un usuario por su correo electrónico, reconstruyendo el objeto
     * correcto (UsuarioClasico o UsuarioPremium) según corresponda.
     *
     * @param email correo a buscar
     * @return el usuario encontrado, o null si no existe
     */
    Usuario buscarPorEmail(String email);

    /**
     * Verifica si ya existe un usuario registrado con ese correo.
     */
    boolean existeEmail(String email);
}