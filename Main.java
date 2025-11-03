package hotel;

public class Main {
    public static void main(String[] args) {
        Habitacion.inicializarHabitaciones();
        DBIniciador.crearTablas();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new VentanaLogin();
        });
    }
}


    