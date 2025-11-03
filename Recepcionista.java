package hotel;

public class Recepcionista extends Empleado {

    public Recepcionista(int id, String nombre, String dni) {
        super(id, nombre, dni);
    }

    public void trabajar() {
        System.out.println("El recepcionista " + getNombre() + " está atendiendo a los huéspedes.");
    }
}



