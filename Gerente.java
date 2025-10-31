package hotel;

public class Gerente extends Empleado {
    public Gerente(int id, String nombre, String dni) {
        super(id, nombre, dni);
    }

    @Override
    public void trabajar() {
        System.out.println("El gerente " + getNombre() + " está supervisando al personal.");
    }

    public void generarReporte() {
        System.out.println("Generando reporte de ocupación del hotel...");
    }

    public void aprobarPresupuesto() {
        System.out.println("Presupuesto aprobado.");
    }
}
