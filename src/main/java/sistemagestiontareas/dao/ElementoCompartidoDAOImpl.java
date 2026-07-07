package sistemagestiontareas.dao;

import sistemagestiontareas.datasource.ConexionBD;
import sistemagestiontareas.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElementoCompartidoDAOImpl implements ElementoCompartidoDAO {

    private ConexionBD conexion;
    private UsuarioDAO usuarioDAO;

    public ElementoCompartidoDAOImpl() {
        this.conexion = ConexionBD.getInstancia();
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    @Override
    public boolean compartir(int elementoId, int usuarioId) {
        Connection con = conexion.getConexion();
        String sql = "INSERT INTO elementos_compartidos (elemento_id, usuario_id) VALUES (?, ?) " +
                "ON CONFLICT DO NOTHING";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, elementoId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error al compartir el elemento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> buscarUsuariosCompartidos(int elementoId) {
        Connection con = conexion.getConexion();
        String sql = "SELECT usuario_id FROM elementos_compartidos WHERE elemento_id = ?";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, elementoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario usuario = usuarioDAO.buscarPorId(rs.getInt("usuario_id"));
                if (usuario != null) usuarios.add(usuario);
            }
            return usuarios;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los usuarios compartidos: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Integer> buscarIdsCompartidosConUsuario(int usuarioId) {
        Connection con = conexion.getConexion();
        String sql = "SELECT elemento_id FROM elementos_compartidos WHERE usuario_id = ?";
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("elemento_id"));
            }
            return ids;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar los elementos compartidos: " + e.getMessage(), e);
        }
    }
        @Override
        public boolean usuarioClasicoYaComparta(int propietarioId) {
            Connection con = conexion.getConexion();
            String sql = "SELECT COUNT(DISTINCT ec.elemento_id) FROM elementos_compartidos ec " +
                    "JOIN elementos e ON e.id = ec.elemento_id WHERE e.usuario_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, propietarioId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                return rs.getInt(1) > 0;

            } catch (SQLException e) {
                throw new RuntimeException("Error al verificar elementos compartidos: " + e.getMessage(), e);
            }
        }
    }