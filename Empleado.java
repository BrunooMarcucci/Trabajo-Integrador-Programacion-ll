package hotel;

public abstract class Empleado extends Persona {

    private String dni;
    private double salario;

    public Empleado(int id, String nombre, String dni) {
        super(id, nombre);
        this.dni = dni;
        this.salario = salario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    // Metodo abstracto que deben implementar las subclases
    public abstract void trabajar();
    
    public void guardar() {
    String sql = "INSERT OR IGNORE INTO Empleado(nombre, dni) VALUES(?, ?, ?)";
    try (java.sql.Connection conn = ConexionSQLite.conectar();
         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, getNombre());
        pstmt.setString(2, getDni());
        pstmt.executeUpdate();
        System.out.println("Empleado guardado correctamente: " + getNombre());

    } catch (java.sql.SQLException e) {
        System.err.println("Error al guardar empleado: " + e.getMessage());
    }
}

}

