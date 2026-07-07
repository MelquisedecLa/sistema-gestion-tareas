package sistemagestiontareas.persistence;

import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Recordatorio;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion concreta de {@link RecordatorioDAO} para PostgreSQL.
 *
 * <p>Gestiona las filas de la tabla {@code elementos} con
 * {@code tipo_elemento = 'RECORDATORIO'} y la tabla {@code elementos_compartidos}.
 * Utiliza la conexion compartida de {@link ConexionBD}.</p>
 */
public class RecordatorioDAOImpl implements RecordatorioDAO {

    /** Referencia al Singleton de conexion. */
    private final ConexionBD conexion = ConexionBD.getInstancia();

    // ── guardar ───────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public int guardar(Recordatorio recordatorio, int usuarioId) throws SQLException {
        String sql = "INSERT INTO elementos (titulo, descripcion, prioridad, fecha_limite, " +
                     "usuario_id, tipo_elemento) VALUES (?, ?, ?, ?, ?, 'RECORDATORIO')";

        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, recordatorio.getTitulo());
            ps.setString(2, recordatorio.getDescripcion());
            ps.setString(3, recordatorio.getPrioridad().name());
            ps.setDate(4, Date.valueOf(recordatorio.getFechaLimite()));
            ps.setInt(5, usuarioId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public Recordatorio buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, descripcion, prioridad, fecha_limite " +
                     "FROM elementos WHERE id = ? AND tipo_elemento = 'RECORDATORIO'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapearRecordatorio(rs);
            }
        }
    }

    // ── buscarPorUsuario ──────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Recordatorio> buscarPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT id, titulo, descripcion, prioridad, fecha_limite " +
                     "FROM elementos " +
                     "WHERE tipo_elemento = 'RECORDATORIO' " +
                     "  AND (usuario_id = ? OR id IN " +
                     "       (SELECT elemento_id FROM elementos_compartidos WHERE usuario_id = ?)) " +
                     "ORDER BY fecha_limite";

        List<Recordatorio> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRecordatorio(rs));
                }
            }
        }
        return lista;
    }

    // ── buscarTodos ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Recordatorio> buscarTodos() throws SQLException {
        String sql = "SELECT id, titulo, descripcion, prioridad, fecha_limite " +
                     "FROM elementos WHERE tipo_elemento = 'RECORDATORIO' ORDER BY fecha_limite";
        List<Recordatorio> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearRecordatorio(rs));
            }
        }
        return lista;
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Recordatorio recordatorio) throws SQLException {
        String sql = "UPDATE elementos SET titulo = ?, descripcion = ?, prioridad = ?, fecha_limite = ? " +
                     "WHERE id = ? AND tipo_elemento = 'RECORDATORIO'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, recordatorio.getTitulo());
            ps.setString(2, recordatorio.getDescripcion());
            ps.setString(3, recordatorio.getPrioridad().name());
            ps.setDate(4, Date.valueOf(recordatorio.getFechaLimite()));
            ps.setInt(5, recordatorio.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM elementos WHERE id = ? AND tipo_elemento = 'RECORDATORIO'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── compartir ─────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean compartir(int elementoId, int usuarioId) throws SQLException {
        String verificar = "SELECT COUNT(*) FROM elementos_compartidos WHERE elemento_id = ? AND usuario_id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(verificar)) {
            ps.setInt(1, elementoId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) return false;
            }
        }

        String sql = "INSERT INTO elementos_compartidos (elemento_id, usuario_id) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, elementoId);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Metodos privados ──────────────────────────────────────────────────────

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Recordatorio}.
     *
     * @param rs resultado posicionado en la fila a leer
     * @return instancia de {@link Recordatorio} con los datos de la fila
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Recordatorio mapearRecordatorio(ResultSet rs) throws SQLException {
        int       id          = rs.getInt("id");
        String    titulo      = rs.getString("titulo");
        String    descripcion = rs.getString("descripcion");
        Prioridad prioridad   = Prioridad.valueOf(rs.getString("prioridad"));
        java.time.LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();
        return new Recordatorio(id, titulo, descripcion, prioridad, fechaLimite);
    }
}
