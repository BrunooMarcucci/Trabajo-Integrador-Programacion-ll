package hotel;

public class Main {
    public static void main(String[] args) {
        DBIniciador.crearTablas();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new VentanaLogin();
        });
    }
}


    