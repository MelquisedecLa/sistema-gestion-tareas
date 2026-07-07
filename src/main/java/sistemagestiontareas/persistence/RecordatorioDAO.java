package sistemagestiontareas.persistence;

import sistemagestiontareas.model.Recordatorio;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad {@link Recordatorio}.
 *
 * <p>Define las operaciones CRUD para recordatorios, incluyendo el mecanismo
 * de compartir. Trabaja sobre la tabla {@code elementos} (con
 * {@code tipo_elemento = 'RECORDATORIO'}) y la tabla {@code elementos_compartidos}.</p>
 */
public interface RecordatorioDAO {

    /**
     * Inserta un nuevo recordatorio en la base de datos.
     *
     * @param recordatorio objeto con los datos a persistir
     * @param usuarioId    id del usuario propietario
     * @return id generado por la base de datos
     * @throws SQLException si falla la insercion
     */
    int guardar(Recordatorio recordatorio, int usuarioId) throws SQLException;

    /**
     * Busca un recordatorio por su clave primaria.
     *
     * @param id identificador del recordatorio
     * @return el {@link Recordatorio} encontrado, o {@code null} si no existe
     * @throws SQLException si falla la consulta
     */
    Recordatorio buscarPorId(int id) throws SQLException;

    /**
     * Retorna todos los recordatorios visibles para un usuario (propios y compartidos).
     *
     * @param usuarioId id del usuario
     * @return lista de recordatorios del usuario
     * @throws SQLException si falla la consulta
     */
    List<Recordatorio> buscarPorUsuario(int usuarioId) throws SQLException;

    /**
     * Retorna todos los recordatorios registrados en el sistema.
     *
     * @return lista de todos los recordatorios
     * @throws SQLException si falla la consulta
     */
    List<Recordatorio> buscarTodos() throws SQLException;

    /**
     * Actualiza los datos de un recordatorio existente (incluida la fecha limite).
     *
     * @param recordatorio objeto con el id y los nuevos datos
     * @return {@code true} si se actualizo al menos una fila
     * @throws SQLException si falla la actualizacion
     */
    boolean actualizar(Recordatorio recordatorio) throws SQLException;

    /**
     * Elimina un recordatorio por su id.
     *
     * @param id identificador del recordatorio a eliminar
     * @return {@code true} si se elimino al menos una fila
     * @throws SQLException si falla la eliminacion
     */
    boolean eliminar(int id) throws SQLException;

    /**
     * Registra que un recordatorio fue compartido con otro usuario.
     *
     * @param elementoId id del recordatorio
     * @param usuarioId  id del usuario destino
     * @return {@code true} si se inserto el registro (false si ya existia)
     * @throws SQLException si falla la operacion
     */
    boolean compartir(int elementoId, int usuarioId) throws SQLException;
}
