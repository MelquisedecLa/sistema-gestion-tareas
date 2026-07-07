package sistemagestiontareas.persistence;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Tarea;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion concreta de {@link TareaDAO} para PostgreSQL.
 *
 * <p>Gestiona las filas de la tabla {@code elementos} con
 * {@code tipo_elemento = 'TAREA'} y la tabla {@code elementos_compartidos}
 * para el mecanismo de compartir. Utiliza la conexion compartida de
 * {@link ConexionBD}.</p>
 */
public class TareaDAOImpl implements TareaDAO {

    /** Referencia al Singleton de conexion. */
    private final ConexionBD conexion = ConexionBD.getInstancia();

    // ── guardar ───────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public int guardar(Tarea tarea, int usuarioId) throws SQLException {
        String sql = "INSERT INTO elementos (titulo, descripcion, prioridad, fecha_limite, " +
                     "usuario_id, tipo_elemento, estado) VALUES (?, ?, ?, ?, ?, 'TAREA', ?)";

        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setString(3, tarea.getPrioridad().name());
            ps.setDate(4, Date.valueOf(tarea.getFechaLimite()));
            ps.setInt(5, usuarioId);
            ps.setString(6, tarea.getEstado() != null ? tarea.getEstado().name() : Estado.PENDIENTE.name());
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
    public Tarea buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, descripcion, prioridad, fecha_limite, estado " +
                     "FROM elementos WHERE id = ? AND tipo_elemento = 'TAREA'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapearTarea(rs);
            }
        }
    }

    // ── buscarPorUsuario ──────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Tarea> buscarPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT id, titulo, descripcion, prioridad, fecha_limite, estado " +
                     "FROM elementos " +
                     "WHERE tipo_elemento = 'TAREA' " +
                     "  AND (usuario_id = ? OR id IN " +
                     "       (SELECT elemento_id FROM elementos_compartidos WHERE usuario_id = ?)) " +
                     "ORDER BY fecha_limite";

        List<Tarea> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTarea(rs));
                }
            }
        }
        return lista;
    }

    // ── buscarTodos ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Tarea> buscarTodos() throws SQLException {
        String sql = "SELECT id, titulo, descripcion, prioridad, fecha_limite, estado " +
                     "FROM elementos WHERE tipo_elemento = 'TAREA' ORDER BY fecha_limite";
        List<Tarea> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearTarea(rs));
            }
        }
        return lista;
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Tarea tarea) throws SQLException {
        String sql = "UPDATE elementos SET titulo = ?, descripcion = ?, prioridad = ?, " +
                     "fecha_limite = ?, estado = ? WHERE id = ? AND tipo_elemento = 'TAREA'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setString(3, tarea.getPrioridad().name());
            ps.setDate(4, Date.valueOf(tarea.getFechaLimite()));
            ps.setString(5, tarea.getEstado() != null ? tarea.getEstado().name() : null);
            ps.setInt(6, tarea.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── actualizarEstado ──────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean actualizarEstado(int id, Estado estado) throws SQLException {
        String sql = "UPDATE elementos SET estado = ? WHERE id = ? AND tipo_elemento = 'TAREA'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM elementos WHERE id = ? AND tipo_elemento = 'TAREA'";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── compartir ─────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean compartir(int elementoId, int usuarioId) throws SQLException {
        // Verificar si ya existe
        String verificar = "SELECT COUNT(*) FROM elementos_compartidos WHERE elemento_id = ? AND usuario_id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(verificar)) {
            ps.setInt(1, elementoId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) return false; // ya compartido
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
     * Convierte una fila del {@link ResultSet} en un objeto {@link Tarea}.
     *
     * @param rs resultado posicionado en la fila a leer
     * @return instancia de {@link Tarea} con los datos de la fila
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Tarea mapearTarea(ResultSet rs) throws SQLException {
        int       id          = rs.getInt("id");
        String    titulo      = rs.getString("titulo");
        String    descripcion = rs.getString("descripcion");
        Prioridad prioridad   = Prioridad.valueOf(rs.getString("prioridad"));
        Estado    estado      = Estado.valueOf(rs.getString("estado") != null
                                    ? rs.getString("estado") : "PENDIENTE");
        java.time.LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();
        return new Tarea(id, titulo, descripcion, prioridad, estado, fechaLimite);
    }
}
