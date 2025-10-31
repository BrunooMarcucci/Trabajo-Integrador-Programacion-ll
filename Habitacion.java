package hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public abstract class Habitacion {

    private int id;
    private int numero;
    private double precioBase;
    private boolean disponible;

    public Habitacion(int id, int numero, double precioBase, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.precioBase = precioBase;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public abstract double calcularPrecio();

    public void guardar(String tipo) {
        String sql = "INSERT INTO Habitacion(numero, tipo, precioBase, disponible) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionSQLite.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, getNumero());
            pstmt.setString(2, tipo);
            pstmt.setDouble(3, getPrecioBase());
            pstmt.setBoolean(4, isDisponible());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    setId(rs.getInt(1)); 
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar habitacion: " + e.getMessage());
        }
    }


    public static void listarTodos() {
        String sql = "SELECT * FROM Habitacion";
        try (Connection conn = ConexionSQLite.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | "
                        + rs.getInt("numero") + " | "
                        + rs.getString("tipo") + " | $"
                        + rs.getDouble("precioBase") + " | Disponible="
                        + rs.getBoolean("disponible"));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar habitaciones: " + e.getMessage());
        }
    }
}
