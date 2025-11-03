package hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reserva {

    private int id;
    private Date fechaInicio;
    private Date fechaFin;
    private double costoTotal;
    private Huesped huesped;
    private Habitacion habitacion;

    public Reserva(int id, Date fechaInicio, Date fechaFin, Huesped huesped, Habitacion habitacion, double costoTotal) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.costoTotal = costoTotal;
    }

    // Constructor alternativo sin costo (se calcula automáticamente)
    public Reserva(int id, Date fechaInicio, Date fechaFin, Huesped huesped, Habitacion habitacion) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.costoTotal = calcularCosto();
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

    public double getCostoTotal() {
        return costoTotal;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public double calcularCosto() {
        double precioBase = habitacion.calcularPrecio();

        // Aplicar beneficios según el tipo de huésped o habitación
        if (huesped instanceof BeneficioAdicional baHuesped) {
            return baHuesped.aplicarBeneficio(precioBase);
        } else if (habitacion instanceof BeneficioAdicional baHabitacion) {
            return baHabitacion.aplicarBeneficio(precioBase);
        } else {
            return precioBase;
        }
    }

    public void guardar() {
        String sql = "INSERT INTO Reserva (fechaInicio, fechaFin, costoTotal, idHuesped, idHabitacion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionSQLite.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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



