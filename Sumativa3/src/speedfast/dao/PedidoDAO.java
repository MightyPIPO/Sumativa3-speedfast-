package speedfast.dao;

import speedfast.modelo.Pedido;
import speedfast.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public boolean create(Pedido pedido) {
        String sql = """
                INSERT INTO pedidos (direccion, tipo, estado)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo());
            ps.setString(3, pedido.getEstado());
            return ps.executeUpdate() > 0;
            }
        catch (SQLException e) {
            System.err.println("Error al crear pedido: "
                + e.getMessage());
            return false;
            }
        }

    public List<Pedido> readAll() {
        return readAll(null, null);
        }

    public List<Pedido> readAll(String estado, String tipo) {
        List<Pedido> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1"
        );
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND estado = ?");
        }
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND tipo = ?");
        }
        sql.append(" ORDER BY id");
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int posicion = 1;
            if (estado != null && !estado.isEmpty()) {
                ps.setString(posicion++, estado);
            }
            if (tipo != null && !tipo.isEmpty()) {
                ps.setString(posicion++, tipo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido(
                            rs.getInt("id"),
                            rs.getString("direccion"),
                            rs.getString("tipo"),
                            rs.getString("estado")
                    );
                    lista.add(pedido);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos: "
                + e.getMessage());
        }
        return lista;
    }

    public boolean update(Pedido pedido) {
        String sql = """
                UPDATE pedidos
                SET direccion = ?, tipo = ?, estado = ?
                WHERE id = ?
                """;
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo());
            ps.setString(3, pedido.getEstado());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar pedido: "
                + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pedido: "
                    + e.getMessage());
            return false;
        }
    }
}
