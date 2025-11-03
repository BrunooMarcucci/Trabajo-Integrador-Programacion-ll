package hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Huesped extends Persona {

    private String dni;

    public Huesped(int id, String nombre, String dni) {
        super(id, nombre);
        this.dni = dni;
    }

    public String getDni() {
        return dni;
    }

    // Guarda huesped y asegura obtener su id (si ya existia lo obtiene)
    public void guardar() {
        String sql = "INSERT OR IGNORE INTO Huesped(nombre, dni, esVip) VALUES (?, ?, ?)";
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getNombre());
            pstmt.setString(2, getDni());
            pstmt.setBoolean(3, this instanceof HuespedVIP);
            pstmt.executeUpdate();

            // Obtener ID (ya sea insertado o existente)
            try (PreparedStatement ps2 = conn.prepareStatement("SELECT id FROM Huesped WHERE dni = ?")) {
                ps2.setString(1, getDni());
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        setId(rs.getInt("id"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar huesped: " + e.getMessage());
        }
    }
}


