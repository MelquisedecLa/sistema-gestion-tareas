package sistemagestiontareas.dao;

import sistemagestiontareas.datasource.ConexionBD;
import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Tarea;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAOImpl implements TareaDAO {

    private ConexionBD conexion;

    public TareaDAOImpl() {
        this.conexion = ConexionBD.getInstancia();
    }

    @Override
    public int guardar(Tarea tarea, int usuarioId) {
        Connection con = conexion.getConexion();
        String sql = "INSERT INTO elementos (titulo, descripcion, prioridad, fecha_limite, usuario_id, tipo_elemento, estado) " +
                "VALUES (?, ?, ?, ?, ?, 'TAREA', ?) RETURNING id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setString(3, tarea.getPrioridad().name());
            ps.setDate(4, Date.valueOf(tarea.getFechaLimite()));
            ps.setInt(5, usuarioId);
            ps.setString(6, tarea.getEstado().name());

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la tarea: " + e.getMessage(), e);
        }
    }

    @Override
    public Tarea buscarPorId(int id) {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM elementos WHERE id = ? AND tipo_elemento = 'TAREA' AND activo = true";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapearTarea(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la tarea: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Tarea> buscarPorUsuario(int usuarioId) {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM elementos WHERE usuario_id = ? AND tipo_elemento = 'TAREA' AND activo = true";
        List<Tarea> tareas = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tareas.add(mapearTarea(rs));
            }
            return tareas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las tareas del usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Tarea> buscarTodos() {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM elementos WHERE tipo_elemento = 'TAREA' AND activo = true";
        List<Tarea> tareas = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tareas.add(mapearTarea(rs));
            }
            return tareas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las tareas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Tarea tarea) {
        Connection con = conexion.getConexion();
        String sql = "UPDATE elementos SET titulo = ?, descripcion = ?, prioridad = ?, fecha_limite = ?, estado = ? " +
                "WHERE id = ? AND tipo_elemento = 'TAREA' AND activo = true";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setString(3, tarea.getPrioridad().name());
            ps.setDate(4, Date.valueOf(tarea.getFechaLimite()));
            ps.setString(5, tarea.getEstado().name());
            ps.setInt(6, tarea.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la tarea: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizarEstado(int id, Estado estado) {
        Connection con = conexion.getConexion();
        String sql = "UPDATE elementos SET estado = ? WHERE id = ? AND tipo_elemento = 'TAREA' AND activo = true";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        Connection con = conexion.getConexion();
        String sql = "UPDATE elementos SET activo = false WHERE id = ? AND tipo_elemento = 'TAREA'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la tarea: " + e.getMessage(), e);
        }
    }

    private Tarea mapearTarea(ResultSet rs) throws SQLException {
        return new Tarea(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                Prioridad.valueOf(rs.getString("prioridad")),
                Estado.valueOf(rs.getString("estado")),
                rs.getDate("fecha_limite").toLocalDate()
        );
    }
}