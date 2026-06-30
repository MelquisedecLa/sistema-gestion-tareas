package sistemagestiontareas.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase responsable de proveer una única conexión activa a la base de datos
 * PostgreSQL durante toda la ejecución de la aplicación (patrón Singleton).
 */
public class ConexionBD {

    private static final String URL = "jdbc:postgresql://localhost:5432/ProyectoPOO";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "Luis0729";

    private static ConexionBD instancia;
    private Connection conexion;

    // Constructor privado: nadie fuera de esta clase puede instanciarla.
    private ConexionBD() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Devuelve la única instancia de ConexionBD, creándola si aún no existe.
     */
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Devuelve la conexión activa. Si por algún motivo se cerró, la reabre.
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo restablecer la conexión: " + e.getMessage(), e);
        }
        return conexion;
    }
}