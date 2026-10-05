package speedfast.dao;

import speedfast.modelo.Entrega;
import speedfast.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    public boolean create(Entrega entrega) {
        String sql = """
                INSERT INTO entregas
                (id_pedido, id_repartidor, fecha, hora)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, entrega.getFecha());
            ps.setTime(4, entrega.getHora());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear entrega: "
                + e.getMessage());
            return false;
        }
    }

    public List<Entrega> readAll() {
        List<Entrega> lista = new ArrayList<>();
        String sql = """
                SELECT id, id_pedido, id_repartidor, fecha, hora
                FROM entregas
                ORDER BY id
                """;
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Entrega entrega = new Entrega(
                    rs.getInt("id"),
                    rs.getInt("id_pedido"),
                    rs.getInt("id_repartidor"),
                    rs.getDate("fecha"),
                    rs.getTime("hora")
                );
                lista.add(entrega);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar entregas: "
                + e.getMessage());
        }
        return lista;
    }

    public boolean update(Entrega entrega) {
        String sql = """
                UPDATE entregas
                SET id_pedido = ?,
                    id_repartidor = ?,
                    fecha = ?,
                    hora = ?
                WHERE id = ?
                """;
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, entrega.getFecha());
            ps.setTime(4, entrega.getHora());
            ps.setInt(5, entrega.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar entrega: "
                + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar entrega: "
                + e.getMessage());
            return false;
        }
    }
}
