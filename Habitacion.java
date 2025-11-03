package hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class Habitacion {

    protected int id;
    protected int numero;
    protected double precioBase;
    protected boolean disponible;

    public Habitacion(int id, int numero, double precioBase, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.precioBase = precioBase;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumero() { return numero; }
    public double getPrecioBase() { return precioBase; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public abstract double calcularPrecio();

    // Sobrecarga: precio por dias (usa polimorfismo)
    public double calcularPrecio(int dias) {
        return calcularPrecio() * dias;
    }

    // Inicializa habitaciones en la BD si no existen 
    public static void inicializarHabitaciones() {
        try (Connection conn = ConexionSQLite.conectar()) {
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) as c FROM Habitacion");
            ResultSet rs = check.executeQuery();
            int count = rs.next() ? rs.getInt("c") : 0;
            rs.close();
            check.close();
            if (count > 0) return; // ya inicializado

            // 10 simples 101-110
            for (int i = 101; i <= 110; i++) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Habitacion(numero,tipo,precioBase,disponible) VALUES(?,?,?,1)");
                ps.setInt(1, i); ps.setString(2, "Simple"); ps.setDouble(3, 2000); ps.executeUpdate(); ps.close();
            }
            // 7 dobles 201-207
            for (int i = 201; i <= 207; i++) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Habitacion(numero,tipo,precioBase,disponible) VALUES(?,?,?,1)");
                ps.setInt(1, i); ps.setString(2, "Doble"); ps.setDouble(3, 3500); ps.executeUpdate(); ps.close();
            }
            // 3 suites 301-303
            for (int i = 301; i <= 303; i++) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Habitacion(numero,tipo,precioBase,disponible) VALUES(?,?,?,1)");
                ps.setInt(1, i); ps.setString(2, "Suite"); ps.setDouble(3, 5000); ps.executeUpdate(); ps.close();
            }
            // 1 presidencial 401
            {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Habitacion(numero,tipo,precioBase,disponible) VALUES(?,?,?,1)");
                ps.setInt(1, 401); ps.setString(2, "SuitePresidencial"); ps.setDouble(3, 8000); ps.executeUpdate(); ps.close();
            }

        } catch (Exception e) {
            System.err.println("Error inicializando habitaciones: " + e.getMessage());
        }
    }

    // Busca primera habitacion disponible del tipo en BD, la marca como ocupada y devuelve instancia con id
    public static Habitacion obtenerDisponible(String tipo) {
        try (Connection conn = ConexionSQLite.conectar()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, numero, precioBase FROM Habitacion WHERE tipo = ? AND disponible = 1 LIMIT 1");
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                int numero = rs.getInt("numero");
                double precio = rs.getDouble("precioBase");
                rs.close(); ps.close();

                PreparedStatement upd = conn.prepareStatement("UPDATE Habitacion SET disponible = 0 WHERE id = ?");
                upd.setInt(1, id); upd.executeUpdate(); upd.close();

                Habitacion h = null;
                switch (tipo) {
                    case "Simple": h = new Simple(id, numero, precio, false); break;
                    case "Doble": h = new Doble(id, numero, precio, false, 1); break;
                    case "Suite": h = new Suite(id, numero, precio, false, true); break;
                    case "SuitePresidencial": h = new SuitePresidencial(id, numero, precio, false, true, true); break;
                    default: break;
                }
                return h;
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("Error obteniendo disponible: " + e.getMessage());
        }
        return null;
    }

    // Libera la habitacion en BD (marcar disponible = 1)
    public static void liberar(Habitacion h) {
        if (h == null) return;
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement("UPDATE Habitacion SET disponible = 1 WHERE id = ?")) {
            ps.setInt(1, h.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error liberando habitacion: " + e.getMessage());
        }
    }
}

