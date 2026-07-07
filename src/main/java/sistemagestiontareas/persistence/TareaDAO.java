package sistemagestiontareas.persistence;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.model.Tarea;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad {@link Tarea}.
 *
 * <p>Define las operaciones CRUD para tareas, incluyendo la gestion de estado
 * y el mecanismo de compartir con otros usuarios. Trabaja sobre la tabla
 * {@code elementos} (con {@code tipo_elemento = 'TAREA'}) y la tabla
 * {@code elementos_compartidos}.</p>
 */
public interface TareaDAO {

    /**
     * Inserta una nueva tarea en la base de datos.
     *
     * @param tarea     objeto con los datos a persistir
     * @param usuarioId id del usuario propietario
     * @return id generado por la base de datos
     * @throws SQLException si falla la insercion
     */
    int guardar(Tarea tarea, int usuarioId) throws SQLException;

    /**
     * Busca una tarea por su clave primaria.
     *
     * @param id identificador de la tarea
     * @return la {@link Tarea} encontrada, o {@code null} si no existe
     * @throws SQLException si falla la consulta
     */
    Tarea buscarPorId(int id) throws SQLException;

    /**
     * Retorna todas las tareas visibles para un usuario (propias y compartidas).
     *
     * @param usuarioId id del usuario
     * @return lista de tareas del usuario
     * @throws SQLException si falla la consulta
     */
    List<Tarea> buscarPorUsuario(int usuarioId) throws SQLException;

    /**
     * Retorna todas las tareas registradas en el sistema.
     *
     * @return lista de todas las tareas
     * @throws SQLException si falla la consulta
     */
    List<Tarea> buscarTodos() throws SQLException;

    /**
     * Actualiza los datos de una tarea existente.
     *
     * @param tarea objeto con el id y los nuevos datos
     * @return {@code true} si se actualizo al menos una fila
     * @throws SQLException si falla la actualizacion
     */
    boolean actualizar(Tarea tarea) throws SQLException;

    /**
     * Actualiza el estado de una tarea.
     *
     * @param id     id de la tarea
     * @param estado nuevo estado a persistir
     * @return {@code true} si se actualizo al menos una fila
     * @throws SQLException si falla la actualizacion
     */
    boolean actualizarEstado(int id, Estado estado) throws SQLException;

    /**
     * Elimina una tarea por su id.
     *
     * @param id identificador de la tarea a eliminar
     * @return {@code true} si se elimino al menos una fila
     * @throws SQLException si falla la eliminacion
     */
    boolean eliminar(int id) throws SQLException;

    /**
     * Registra que una tarea fue compartida con otro usuario.
     *
     * @param elementoId id de la tarea
     * @param usuarioId  id del usuario destino
     * @return {@code true} si se inserto el registro (false si ya existia)
     * @throws SQLException si falla la operacion
     */
    boolean compartir(int elementoId, int usuarioId) throws SQLException;
}
