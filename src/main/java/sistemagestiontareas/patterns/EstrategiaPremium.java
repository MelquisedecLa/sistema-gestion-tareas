package sistemagestiontareas.patterns;

import sistemagestiontareas.model.Elemento;
import java.util.List;

/**
 * Estrategia de creación para usuario premium.
 * No tiene restricciones en la cantidad de elementos.
 */
public class EstrategiaPremium implements EstrategiaCreacion {

    @Override
    public boolean crearElemento(Elemento elemento, List<Elemento> elementos) {
        System.out.println("Elemento creado con estrategia premium (sin límite).");
        return true;
    }
}