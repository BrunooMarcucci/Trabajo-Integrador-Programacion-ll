package hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class Factura {
    private int id;
    private Date fecha;
    private double montoTotal;

    public Factura(int id, Date fecha, double montoTotal) {
        this.id = id;
        this.fecha = fecha;
        this.montoTotal = montoTotal;
    }

    public int getId() { return id; }
    public Date getFecha() { return fecha; }
    public double getMontoTotal() { return montoTotal; }

    public void guardar(int idReserva) {
        String sql = "INSERT INTO Factura(fecha, montoTotal, idReserva) VALUES(?, ?, ?)";
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fecha.toString());
            pstmt.setDouble(2, montoTotal);
            pstmt.setInt(3, idReserva);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) id = keys.getInt(1);
            }
            System.out.println("Factura guardada. Monto: $" + montoTotal);
        } catch (SQLException e) {
            System.out.println("Error al guardar factura: " + e.getMessage());
        }
    }

    public static void listarTodos() {
        String sql = "SELECT * FROM Factura";
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Factura #" + rs.getInt("id") + " | $" +
                                   rs.getDouble("montoTotal") + " | Fecha: " +
                                   rs.getString("fecha"));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar facturas: " + e.getMessage());
        }
    }
    
    public static void generarFactura(Scanner sc) {
        try {
            System.out.print("Ingrese el nombre del huesped: ");
            String nombre = sc.nextLine();

            String sql = "SELECT r.id, r.costoTotal FROM Reserva r " +
                         "JOIN Huesped h ON r.idHuesped = h.id " +
                         "WHERE h.nombre = ? ORDER BY r.id DESC LIMIT 1";

            try (Connection conn = ConexionSQLite.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int idReserva = rs.getInt("id");
                    double monto = rs.getDouble("costoTotal");

                    Factura factura = new Factura(0, new Date(), monto);
                    factura.guardar(idReserva);
                    System.out.println("Factura generada correctamente. Monto a pagar: $" + monto);
                } else {
                    System.out.println("No se encontro ninguna reserva para ese huesped.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error generando factura: " + e.getMessage());
        }
    }
}
