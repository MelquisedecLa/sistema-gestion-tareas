package sistemagestiontareas.dao;

import sistemagestiontareas.datasource.ConexionBD;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;
import sistemagestiontareas.patterns.FormaPago;
import sistemagestiontareas.patterns.Paypal;
import sistemagestiontareas.patterns.TarjetaCredito;
import sistemagestiontareas.patterns.TarjetaDebito;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class UsuarioDAOImpl implements UsuarioDAO {

    // Guardamos la conexión aquí para no llamar a ConexionBD.getInstancia() en cada método
    private ConexionBD conexion;

    public UsuarioDAOImpl() {
        this.conexion = ConexionBD.getInstancia();
    }

    @Override
    public int guardar(Usuario usuario) {
        Connection con = conexion.getConexion();

        String sqlUsuario = "INSERT INTO usuarios (nombre, email, password, tipo_usuario, limite_elementos, fecha_expiracion) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        int idGenerado;

        try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());

            if (usuario instanceof UsuarioClasico clasico) {
                ps.setString(4, "CLASICO");
                ps.setInt(5, clasico.getLimiteElementos());
                ps.setNull(6, Types.DATE);
            } else if (usuario instanceof UsuarioPremium premium) {
                ps.setString(4, "PREMIUM");
                ps.setNull(5, Types.INTEGER);
                ps.setDate(6, Date.valueOf(premium.getFechaExpiracion()));
            } else {
                throw new IllegalArgumentException("Tipo de usuario no soportado.");
            }

            ResultSet rs = ps.executeQuery();
            rs.next();
            idGenerado = rs.getInt("id");

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
        }

        // Si es premium y tiene método de pago, lo guardamos también.
        if (usuario instanceof UsuarioPremium && usuario.getMetodoPago() != null) {
            guardarFormaPago(idGenerado, usuario.getMetodoPago());
        }

        return idGenerado;
    }

    private void guardarFormaPago(int usuarioId, FormaPago formaPago) {
        Connection con = conexion.getConexion();
        String sql = "INSERT INTO formas_pago (usuario_id, tipo, email_paypal, contrasena_paypal, " +
                "numero_tarjeta, fecha_expiracion_tarjeta, titular, cvv) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);

            if (formaPago instanceof Paypal paypal) {
                ps.setString(2, "PAYPAL");
                ps.setString(3, paypal.getEmail());
                ps.setString(4, paypal.getContrasena());
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.DATE);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.INTEGER);

            } else if (formaPago instanceof TarjetaCredito tc) {
                ps.setString(2, "CREDITO");
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
                ps.setString(5, tc.getNumeroTarjeta());
                ps.setDate(6, Date.valueOf(tc.getFechaExpiracion()));
                ps.setString(7, tc.getTitular());
                ps.setInt(8, tc.getCvv());

            } else if (formaPago instanceof TarjetaDebito td) {
                ps.setString(2, "DEBITO");
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
                ps.setString(5, td.getNumeroTarjeta());
                ps.setDate(6, Date.valueOf(td.getFechaExpiracion()));
                ps.setString(7, td.getTitular());
                ps.setInt(8, td.getCvv());
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la forma de pago: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existeEmail(String email) {
        Connection con = conexion.getConexion();
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el correo: " + e.getMessage(), e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String tipo = rs.getString("tipo_usuario");

        if ("PREMIUM".equals(tipo)) {
            LocalDate fechaExpiracion = rs.getDate("fecha_expiracion").toLocalDate();
            return new UsuarioPremium(nombre, id, email, password, fechaExpiracion);
        } else {
            return new UsuarioClasico(nombre, id, email, password);
        }
    }
    @Override
    public Usuario buscarPorId(int id) {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> buscarTodos() {
        Connection con = conexion.getConexion();
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            return usuarios;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los usuarios: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        Connection con = conexion.getConexion();
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.setInt(4, usuario.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        Connection con = conexion.getConexion();
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el usuario: " + e.getMessage(), e);
        }
    }
}