package sistemagestiontareas.interfaces;

import sistemagestiontareas.model.Usuario;

/**
 * Define la capacidad de compartir un elemento con otro usuario.
 */
public interface Compartible {

    /**
     * Permite compartir un elemento con un usuario específico.
     *
     * @param usuario usuario con quien se compartirá el elemento
     */
    void compartir(Usuario usuario);
}