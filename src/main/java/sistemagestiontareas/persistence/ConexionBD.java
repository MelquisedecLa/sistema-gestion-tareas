package sistemagestiontareas.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Punto unico de acceso a la base de datos PostgreSQL (patron Singleton).
 *
 * <p>Mantiene una unica instancia de {@link Connection} compartida por todos
 * los DAO del sistema ({@link UsuarioDAOImpl}, {@link TareaDAOImpl},
 * {@link RecordatorioDAOImpl}). La conexion se abre al crear la instancia
 * mediante {@link #conectar()} y puede cerrarse con {@link #desconectar()}.</p>
 *
 * <p>Las credenciales se leen en orden de prioridad desde:</p>
 * <ol>
 *   <li>{@code db.properties} en el classpath ({@code src/main/resources}).</li>
 *   <li>Variables de entorno {@code DB_URL}, {@code DB_USER}, {@code DB_PASSWORD}.</li>
 *   <li>Valores por defecto: {@code localhost:5432/GestionTareasDB}.</li>
 * </ol>
 *
 * <p>Al conectar por primera vez se ejecuta {@link #verificarEsquema()} para
 * crear las tablas si no existen, usando {@code CREATE TABLE IF NOT EXISTS}.</p>
 */
public final class ConexionBD {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static ConexionBD instancia;

    // ── Configuracion de conexion (static final, leidas al cargar la clase) ───

    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        Properties props = cargarPropiedades();
        URL      = props.getProperty("db.url",
                System.getenv().getOrDefault("DB_URL",
                        "jdbc:postgresql://localhost:5432/GestionTareasDB"));
        USUARIO  = props.getProperty("db.user",
                System.getenv().getOrDefault("DB_USER", "postgres"));
        PASSWORD = props.getProperty("db.password",
                System.getenv().getOrDefault("DB_PASSWORD", ""));
    }

    // ── Conexion compartida ───────────────────────────────────────────────────

    /** Conexion JDBC compartida por todos los DAO. */
    private Connection conexion;

    // ── Constructor privado ───────────────────────────────────────────────────

    /**
     * Constructor privado. Abre la conexion y verifica el esquema.
     */
    private ConexionBD() {
        conectar();
    }

    // ── Acceso al Singleton ───────────────────────────────────────────────────

    /**
     * Retorna la unica instancia de {@code ConexionBD}, creandola si no existe.
     * Sincronizado para entornos multi-hilo.
     *
     * @return instancia Singleton de {@code ConexionBD}
     */
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // ── API publica ───────────────────────────────────────────────────────────

    /**
     * Retorna la conexion JDBC activa compartida por los DAO.
     * Si la conexion esta cerrada o es nula, intenta reconectar.
     *
     * @return {@link Connection} activa con la base de datos
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            conectar();
        }
        return conexion;
    }

    /**
     * Abre la conexion JDBC con PostgreSQL usando las credenciales configuradas
     * y verifica que el esquema de tablas exista.
     *
     * @throws RuntimeException si no puede establecer la conexion
     */
    public void conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            verificarEsquema();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a PostgreSQL: " + e.getMessage(), e);
        }
    }

    /**
     * Cierra la conexion JDBC con la base de datos.
     */
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("[ConexionBD] Error al desconectar: " + e.getMessage());
        }
    }

    // ── Metodos privados ──────────────────────────────────────────────────────

    /**
     * Crea las tablas del sistema si no existen, usando {@code CREATE TABLE IF NOT EXISTS}
     * de PostgreSQL. Incluye: {@code usuarios}, {@code formas_pago}, {@code elementos},
     * {@code elementos_compartidos}.
     *
     * @throws SQLException si alguna sentencia DDL falla
     */
    private void verificarEsquema() throws SQLException {
        String crearUsuarios =
            "CREATE TABLE IF NOT EXISTS usuarios (" +
            "    id               SERIAL PRIMARY KEY," +
            "    nombre           VARCHAR(100) NOT NULL," +
            "    email            VARCHAR(150) NOT NULL UNIQUE," +
            "    password         VARCHAR(100) NOT NULL," +
            "    tipo_usuario     VARCHAR(20)  NOT NULL CHECK (tipo_usuario IN ('CLASICO','PREMIUM'))," +
            "    limite_elementos INTEGER," +
            "    fecha_expiracion DATE" +
            ")";

        String crearFormasPago =
            "CREATE TABLE IF NOT EXISTS formas_pago (" +
            "    id                       SERIAL PRIMARY KEY," +
            "    usuario_id               INTEGER NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE," +
            "    tipo                     VARCHAR(20) NOT NULL CHECK (tipo IN ('PAYPAL','CREDITO','DEBITO'))," +
            "    email_paypal             VARCHAR(150)," +
            "    contrasena_paypal        VARCHAR(100)," +
            "    numero_tarjeta           VARCHAR(16)," +
            "    fecha_expiracion_tarjeta DATE," +
            "    titular                  VARCHAR(100)," +
            "    cvv                      INTEGER" +
            ")";

        String crearElementos =
            "CREATE TABLE IF NOT EXISTS elementos (" +
            "    id            SERIAL PRIMARY KEY," +
            "    titulo        VARCHAR(150) NOT NULL," +
            "    descripcion   VARCHAR(500)," +
            "    prioridad     VARCHAR(10)  NOT NULL CHECK (prioridad IN ('ALTA','MEDIA','BAJA'))," +
            "    fecha_limite  DATE NOT NULL," +
            "    usuario_id    INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE," +
            "    tipo_elemento VARCHAR(20)  NOT NULL CHECK (tipo_elemento IN ('TAREA','RECORDATORIO'))," +
            "    estado        VARCHAR(20)  CHECK (estado IN ('PENDIENTE','EN_PROGRESO','COMPLETADA','CANCELADA'))" +
            ")";

        String crearCompartidos =
            "CREATE TABLE IF NOT EXISTS elementos_compartidos (" +
            "    elemento_id INTEGER NOT NULL REFERENCES elementos(id) ON DELETE CASCADE," +
            "    usuario_id  INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE," +
            "    PRIMARY KEY (elemento_id, usuario_id)" +
            ")";

        try (Statement st = conexion.createStatement()) {
            st.execute(crearUsuarios);
            st.execute(crearFormasPago);
            st.execute(crearElementos);
            st.execute(crearCompartidos);
        }
    }

    /**
     * Lee el archivo {@code db.properties} del classpath.
     *
     * @return {@link Properties} con los valores leidos, o vacío si no se encuentra el archivo
     */
    private static Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.out.println("[ConexionBD] No se pudo leer db.properties; usando valores por defecto.");
        }
        return props;
    }
}
