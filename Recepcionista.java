package hotel;

public class Recepcionista extends Empleado {

    public Recepcionista(int id, String nombre, String dni) {
        super(id, nombre, dni);
    }

    @Override
    public void trabajar() {
        System.out.println("El recepcionista " + getNombre() + " esta trabajando.");
    }

    public void registrarReserva(Reserva r) {
        System.out.println("Reserva registrada por el recepcionista " + getNombre());
    }
}


