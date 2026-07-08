package sistemagestiontareas.dao;

import sistemagestiontareas.datasource.ConexionBD;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Recordatorio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordatorioDAOImpl implements RecordatorioDAO {

    private ConexionBD conexion;

    public RecordatorioDAOImpl() {
        this.conexion = ConexionBD.getInstancia();
    }

    @Override
    public int guardar(Recordatorio recordatorio, int usuarioId) {
        Connection con = conexion.getConexion();
        String sql = "INSERT INTO elementos (titulo, descripcion, prioridad, fecha_limite, usuario_id, tipo_elemento) " +
                "VALUES (?, ?, ?, ?, ?, 'RECORDATORIO') RETURNING id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, recordatorio.getTitulo());
            ps.setString(2, recordatorio.getDescripcion());
            ps.setString(3, recordatorio.getPrioridad().name());
            ps.setDate(4, Date.valueOf(recordatorio.getFechaLimite()));
            ps.setInt(5, usuarioId);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el recordatorio: " + e.getMessage(), e);
        }
    }

    @Override
    public Recordatorio buscarPorId(int id) {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM elementos WHERE id = ? AND tipo_elemento = 'RECORDATORIO' AND activo = true";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapearRecordatorio(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el recordatorio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Recordatorio> buscarPorUsuario(int usuarioId) {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM elementos WHERE usuario_id = ? AND tipo_elemento = 'RECORDATORIO' AND activo = true";
        List<Recordatorio> recordatorios = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordatorios.add(mapearRecordatorio(rs));
            }
            return recordatorios;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los recordatorios del usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Recordatorio> buscarTodos() {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM elementos WHERE tipo_elemento = 'RECORDATORIO' AND activo = true";
        List<Recordatorio> recordatorios = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordatorios.add(mapearRecordatorio(rs));
            }
            return recordatorios;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los recordatorios: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Recordatorio recordatorio) {
        Connection con = conexion.getConexion();
        String sql = "UPDATE elementos SET titulo = ?, descripcion = ?, prioridad = ?, fecha_limite = ? " +
                "WHERE id = ? AND tipo_elemento = 'RECORDATORIO' AND activo = true";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, recordatorio.getTitulo());
            ps.setString(2, recordatorio.getDescripcion());
            ps.setString(3, recordatorio.getPrioridad().name());
            ps.setDate(4, Date.valueOf(recordatorio.getFechaLimite()));
            ps.setInt(5, recordatorio.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el recordatorio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        Connection con = conexion.getConexion();
        String sql = "UPDATE elementos SET activo = false WHERE id = ? AND tipo_elemento = 'RECORDATORIO'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el recordatorio: " + e.getMessage(), e);
        }
    }

    private Recordatorio mapearRecordatorio(ResultSet rs) throws SQLException {
        return new Recordatorio(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                Prioridad.valueOf(rs.getString("prioridad")),
                rs.getDate("fecha_limite").toLocalDate()
        );
    }
}