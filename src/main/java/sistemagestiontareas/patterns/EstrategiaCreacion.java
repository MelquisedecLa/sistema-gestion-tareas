package sistemagestiontareas.patterns;

import sistemagestiontareas.model.Elemento;

/**
 * Interfaz del patrón Strategy para la creación de elementos.
 * Define el comportamiento que cada estrategia debe implementar.
 */
public interface EstrategiaCreacion {

    /**
     * Crea un elemento según las reglas de cada estrategia.
     *
     * @param elemento elemento a crear
     * @param elementos lista actual de elementos del usuario
     * @return true si el elemento fue creado, false si no
     */
    boolean crearElemento(Elemento elemento, java.util.List<Elemento> elementos);
}
