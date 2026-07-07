package sistemagestiontareas.persistence;

import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;
import sistemagestiontareas.patterns.FormaPago;
import sistemagestiontareas.patterns.Paypal;
import sistemagestiontareas.patterns.TarjetaCredito;
import sistemagestiontareas.patterns.TarjetaDebito;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion concreta de {@link UsuarioDAO} para PostgreSQL.
 *
 * <p>Gestiona la tabla {@code usuarios} y, para cuentas Premium, la tabla
 * {@code formas_pago}. Utiliza la conexion compartida de {@link ConexionBD}.</p>
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    /** Referencia al Singleton de conexion. */
    private final ConexionBD conexion = ConexionBD.getInstancia();

    // ── guardar ───────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * <p>Para usuarios Premium, inserta tambien el metodo de pago en
     * {@code formas_pago} si esta definido.</p>
     */
    @Override
    public int guardar(Usuario usuario) throws SQLException {
        String tipo = (usuario instanceof UsuarioPremium) ? "PREMIUM" : "CLASICO";
        LocalDate fechaExp = (usuario instanceof UsuarioPremium p) ? p.getFechaExpiracion() : null;
        Integer limite = (usuario instanceof UsuarioClasico c) ? c.getLimiteElementos() : null;

        String sql = "INSERT INTO usuarios (nombre, email, password, tipo_usuario, limite_elementos, fecha_expiracion) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        int idGenerado;
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, tipo);
            if (limite != null) ps.setInt(5, limite); else ps.setNull(5, Types.INTEGER);
            if (fechaExp != null) ps.setDate(6, Date.valueOf(fechaExp)); else ps.setNull(6, Types.DATE);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                idGenerado = rs.getInt(1);
            }
        }

        // Si es Premium y tiene metodo de pago, persistirlo en formas_pago
        if (usuario instanceof UsuarioPremium premium && premium.getMetodoPago() != null) {
            guardarFormaPago(idGenerado, premium.getMetodoPago());
        }

        return idGenerado;
    }

    /**
     * Inserta el metodo de pago de un usuario Premium en la tabla {@code formas_pago}.
     *
     * @param usuarioId  id del usuario ya insertado
     * @param formaPago  instancia de {@link FormaPago} (Paypal, TarjetaCredito, TarjetaDebito)
     * @throws SQLException si falla la insercion
     */
    private void guardarFormaPago(int usuarioId, FormaPago formaPago) throws SQLException {
        String sql = "INSERT INTO formas_pago (usuario_id, tipo, email_paypal, contrasena_paypal, " +
                     "numero_tarjeta, fecha_expiracion_tarjeta, titular, cvv) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
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
        }
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, email, password, tipo_usuario, limite_elementos, fecha_expiracion " +
                     "FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapearUsuario(rs);
            }
        }
    }

    // ── buscarPorEmail ────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nombre, email, password, tipo_usuario, limite_elementos, fecha_expiracion " +
                     "FROM usuarios WHERE email = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapearUsuario(rs);
            }
        }
    }

    // ── buscarTodos ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Usuario> buscarTodos() throws SQLException {
        String sql = "SELECT id, nombre, email, password, tipo_usuario, limite_elementos, fecha_expiracion " +
                     "FROM usuarios ORDER BY nombre";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.setInt(4, usuario.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── existeEmail ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // ── Metodos privados ──────────────────────────────────────────────────────

    /**
     * Convierte una fila del {@link ResultSet} en el objeto de dominio
     * {@link UsuarioClasico} o {@link UsuarioPremium} correspondiente.
     *
     * @param rs resultado de la consulta posicionado en la fila a leer
     * @return instancia de {@link Usuario} del subtipo correcto
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int     id       = rs.getInt("id");
        String  nombre   = rs.getString("nombre");
        String  email    = rs.getString("email");
        String  password = rs.getString("password");
        String  tipo     = rs.getString("tipo_usuario");

        if ("PREMIUM".equals(tipo)) {
            Date fechaSql = rs.getDate("fecha_expiracion");
            LocalDate fechaExp = (fechaSql != null) ? fechaSql.toLocalDate() : LocalDate.now().plusMonths(1);
            return new UsuarioPremium(nombre, id, email, password, fechaExp);
        }
        return new UsuarioClasico(nombre, id, email, password);
    }
}
