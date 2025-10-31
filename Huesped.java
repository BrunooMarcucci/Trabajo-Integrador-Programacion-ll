package hotel;

import java.sql.*;
import java.sql.Statement;

public class Huesped extends Persona {

    private String dni;

    public Huesped(int id, String nombre, String dni) {
        super(id, nombre);
        this.dni = dni;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void guardar() {
        String sqlInsert = "INSERT INTO Huesped(nombre, dni, esVip) VALUES(?, ?, ?)";
        String sqlSelect = "SELECT id FROM Huesped WHERE dni = ?";

        try (Connection conn = ConexionSQLite.conectar()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setString(1, getNombre());
                pstmt.setString(2, getDni());
                pstmt.setBoolean(3, (this instanceof HuespedVIP));
                pstmt.executeUpdate();
            } catch (SQLException e) {
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlSelect)) {
                pstmt2.setString(1, getDni());
                try (ResultSet rs = pstmt2.executeQuery()) {
                    if (rs.next()) {
                        this.setId(rs.getInt("id"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar huesped: " + e.getMessage());
        }
    }
}

