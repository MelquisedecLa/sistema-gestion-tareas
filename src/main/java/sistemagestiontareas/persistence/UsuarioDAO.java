package sistemagestiontareas.persistence;

import sistemagestiontareas.model.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad {@link Usuario}.
 *
 * <p>Define las operaciones CRUD que deben implementar las clases concretas
 * (e.g. {@link UsuarioDAOImpl}). Trabaja sobre la tabla {@code usuarios} y,
 * para cuentas Premium, sobre {@code formas_pago}.</p>
 */
public interface UsuarioDAO {

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param usuario objeto con los datos a persistir (id puede ser 0)
     * @return id generado por la base de datos
     * @throws SQLException si falla la insercion
     */
    int guardar(Usuario usuario) throws SQLException;

    /**
     * Busca un usuario por su clave primaria.
     *
     * @param id identificador del usuario
     * @return el {@link Usuario} encontrado, o {@code null} si no existe
     * @throws SQLException si falla la consulta
     */
    Usuario buscarPorId(int id) throws SQLException;

    /**
     * Busca un usuario por su correo electronico.
     *
     * @param email correo a buscar
     * @return el {@link Usuario} encontrado, o {@code null} si no existe
     * @throws SQLException si falla la consulta
     */
    Usuario buscarPorEmail(String email) throws SQLException;

    /**
     * Retorna todos los usuarios registrados, ordenados por nombre.
     *
     * @return lista (posiblemente vacia) de todos los usuarios
     * @throws SQLException si falla la consulta
     */
    List<Usuario> buscarTodos() throws SQLException;

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuario objeto con el id y los nuevos datos
     * @return {@code true} si se actualizo al menos una fila
     * @throws SQLException si falla la actualizacion
     */
    boolean actualizar(Usuario usuario) throws SQLException;

    /**
     * Elimina un usuario por su id.
     *
     * @param id identificador del usuario a eliminar
     * @return {@code true} si se elimino al menos una fila
     * @throws SQLException si falla la eliminacion
     */
    boolean eliminar(int id) throws SQLException;

    /**
     * Verifica si ya existe un usuario con el correo indicado.
     *
     * @param email correo a verificar
     * @return {@code true} si el correo ya esta registrado
     * @throws SQLException si falla la consulta
     */
    boolean existeEmail(String email) throws SQLException;
}
