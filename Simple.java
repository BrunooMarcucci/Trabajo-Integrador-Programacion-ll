package hotel;

public class Simple extends Habitacion {

    public Simple(int id, int numero, double precioBase, boolean disponible) {
        super(id, numero, precioBase, disponible);
    }

    @Override
    public double calcularPrecio() {
        return getPrecioBase();
    }

    // Sobrecarga — permite calcular el precio por varios días
    public double calcularPrecio(int dias) {
        return getPrecioBase() * dias;
    }
}
