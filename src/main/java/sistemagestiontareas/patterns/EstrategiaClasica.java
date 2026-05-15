package sistemagestiontareas.patterns;

import sistemagestiontareas.model.Elemento;
import java.util.List;

/**
 * Estrategia de creación para usuario clásico.
 * Limita la cantidad máxima de elementos a crear.
 */
public class EstrategiaClasica implements EstrategiaCreacion {

    private static final int LIMITE = 3;

    @Override
    public boolean crearElemento(Elemento elemento, List<Elemento> elementos) {
        if (elementos.size() < LIMITE) {
            System.out.println("Elemento creado con estrategia clásica.");
            return true;
        }
        System.out.println("Límite de " + LIMITE + " elementos alcanzado.");
        return false;
    }
}