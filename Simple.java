package hotel;

public class Simple extends Habitacion {
    public Simple(int id, int numero, double precioBase, boolean disponible) {
        super(id, numero, precioBase, disponible);
    }

    @Override
    public double calcularPrecio() {
        return getPrecioBase();
    }
}
