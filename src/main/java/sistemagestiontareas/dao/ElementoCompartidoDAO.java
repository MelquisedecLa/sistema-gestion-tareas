package sistemagestiontareas.dao;

import sistemagestiontareas.model.Usuario;
import java.util.List;

/**
 * Contrato de acceso a datos para la tabla {@code elementos_compartidos}.
 *
 * <p>Gestiona el mecanismo de compartir elementos entre usuarios.
 * Un {@code UsuarioClasico} solo puede compartir cada elemento con un maximo
 * de un usuario; un {@code UsuarioPremium} no tiene esa restriccion.</p>
 */
public interface ElementoCompartidoDAO {

    /**
     * Registra que el elemento indicado es compartido con el usuario indicado.
     *
     * @param elementoId id del elemento a compartir
     * @param usuarioId  id del usuario destinatario
     * @return {@code true} si se insertó correctamente
     * @throws RuntimeException si falla la operacion
     */
    boolean usuarioClasicoYaComparta(int propietarioId);
    boolean compartir(int elementoId, int usuarioId);

    /**
     * Retorna la lista de usuarios con quienes se ha compartido el elemento.
     *
     * @param elementoId id del elemento
     * @return lista de {@link sistemagestiontareas.model.Usuario} (puede estar vacia)
     * @throws RuntimeException si falla la consulta
     */
    List<Usuario> buscarUsuariosCompartidos(int elementoId);

    /**
     * Retorna los ids de elementos que han sido compartidos con el usuario indicado.
     *
     * @param usuarioId id del usuario
     * @return lista de ids de elementos compartidos con ese usuario
     * @throws RuntimeException si falla la consulta
     */
    List<Integer> buscarIdsCompartidosConUsuario(int usuarioId);

    /**
     * Cuenta cuantos usuarios tienen acceso compartido al elemento indicado.
     * Usado para validar el limite de compartir del {@code UsuarioClasico}.
     *
     * @param elementoId id del elemento
     * @return numero de usuarios con quienes se ha compartido el elemento
     * @throws RuntimeException si falla la consulta
     */

    int contarUsuariosCompartidos(int elementoId);
}