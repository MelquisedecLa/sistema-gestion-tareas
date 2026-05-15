package sistemagestiontareas.model;

/**
 * Clase encargada de validar el formato y dominio de un correo electrónico.
 */
public class ValidadorCorreo {

    /**
     * Verifica que el email contenga '@' y no esté vacío.
     *
     * @param email correo a validar
     * @return true si el formato es válido
     */
    public boolean validarFormato(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.contains("@") && email.indexOf("@") > 0;
    }

    /**
     * Verifica que el email tenga un dominio válido después del '@'.
     *
     * @param email correo a validar
     * @return true si el dominio es válido
     */
    public boolean validarDominio(String email) {
        if (!validarFormato(email)) return false;
        String dominio = email.substring(email.indexOf("@") + 1);
        return dominio.contains(".") && dominio.length() > 3;
    }
}