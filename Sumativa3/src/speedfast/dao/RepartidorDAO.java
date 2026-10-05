package speedfast.dao;

import speedfast.modelo.Repartidor;
import speedfast.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    public boolean create(Repartidor repartidor) {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, repartidor.getNombre());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            System.err.println("Error al crear repartidor: " + e.getMessage());
            return false;
        }
    }

    public List<Repartidor> readAll() {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Repartidor repartidor = new Repartidor(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                lista.add(repartidor);
                }
            }
        catch (SQLException e) {
            System.err.println("Error al listar repartidores: "
                + e.getMessage());
            }
        return lista;
    }

    public boolean update(Repartidor repartidor) {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, repartidor.getNombre());
                ps.setInt(2, repartidor.getId());
                return ps.executeUpdate() > 0;
                }
        catch (SQLException e) {
            System.err.println("Error al actualizar repartidor: "
                + e.getMessage());
            return false;
            }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM repartidores WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            }
        catch (SQLException e) {
            System.err.println("Error al eliminar repartidor: "
                + e.getMessage());
            return false;
            }
        }
    }
