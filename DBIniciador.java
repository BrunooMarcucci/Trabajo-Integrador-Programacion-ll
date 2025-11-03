package hotel;

import java.sql.Connection;
import java.sql.Statement;

public class DBIniciador {

    public static void crearTablas() {
        try (Connection conn = ConexionSQLite.conectar(); Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Factura");
            stmt.execute("DROP TABLE IF EXISTS Reserva");
            stmt.execute("DROP TABLE IF EXISTS Habitacion");
            stmt.execute("DROP TABLE IF EXISTS Huesped");
            stmt.execute("DROP TABLE IF EXISTS Empleado");

            stmt.execute("CREATE TABLE Huesped ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL,"
                    + "dni TEXT NOT NULL UNIQUE,"
                    + "esVip BOOLEAN NOT NULL)");

            stmt.execute("CREATE TABLE Habitacion ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "numero INTEGER NOT NULL UNIQUE,"
                    + "tipo TEXT NOT NULL,"
                    + "precioBase REAL NOT NULL,"
                    + "disponible BOOLEAN NOT NULL)");

            stmt.execute("CREATE TABLE Reserva ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "fechaInicio TEXT NOT NULL,"
                    + "fechaFin TEXT NOT NULL,"
                    + "costoTotal REAL NOT NULL,"
                    + "idHuesped INTEGER,"
                    + "idHabitacion INTEGER,"
                    + "FOREIGN KEY(idHuesped) REFERENCES Huesped(id),"
                    + "FOREIGN KEY(idHabitacion) REFERENCES Habitacion(id))");

            stmt.execute("CREATE TABLE Factura ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "fecha TEXT NOT NULL,"
                    + "montoTotal REAL NOT NULL,"
                    + "idReserva INTEGER,"
                    + "FOREIGN KEY(idReserva) REFERENCES Reserva(id))");

            String sqlEmpleado = """
                    CREATE TABLE IF NOT EXISTS Empleado (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    salario REAL
                    );""";
            stmt.execute(sqlEmpleado);

            // Las habitaciones las inicializa Habitacion.inicializarHabitaciones() después

        } catch (Exception e) {
            System.out.println("Error recreando tablas: " + e.getMessage());
        }
    }
}



