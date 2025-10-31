package hotel;


import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class Reserva {

    private int id;
    private Date fechaInicio;
    private Date fechaFin;
    private double costoTotal;
    private Huesped huesped;
    private Habitacion habitacion;
    private Connection conn;

    public Reserva(int id, Date fechaInicio, Date fechaFin, Huesped huesped, Habitacion habitacion) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.huesped = huesped;
        this.habitacion = habitacion;
        if (huesped instanceof HuespedVIP) {
            double d = ((HuespedVIP) huesped).getDescuento();
            this.costoTotal = habitacion.calcularPrecio() * (1 - d);
        } else {
            this.costoTotal = habitacion.calcularPrecio();
        }
    }

    public int getId() {
        return id;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public double calcularCosto() {
        return costoTotal;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void guardar() {
        String sql = "INSERT INTO Reserva(fechaInicio, fechaFin, costoTotal, idHuesped, idHabitacion) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = ConexionSQLite.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(fechaInicio));
            pstmt.setString(2, sdf.format(fechaFin));
            pstmt.setDouble(3, calcularCosto());
            pstmt.setInt(4, huesped.getId());
            pstmt.setInt(5, habitacion.getId()); 
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
        }
    }

}
