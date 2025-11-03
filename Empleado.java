package hotel;

import java.util.ArrayList;
import java.util.List;

public class Empleado extends Persona {
    private String dni;
    private List<Reserva> reservas; // ← AGREGACIÓN: el empleado tiene reservas, pero no las posee

    public Empleado(int id, String nombre, String dni) {
        super(id, nombre);
        this.dni = dni;
        this.reservas = new ArrayList<>();
    }

    // Método para agregar una reserva gestionada por el empleado
    public void agregarReserva(Reserva r) {
        reservas.add(r);
    }

    // Obtener las reservas gestionadas
    public List<Reserva> getReservas() {
        return reservas;
    }
}


