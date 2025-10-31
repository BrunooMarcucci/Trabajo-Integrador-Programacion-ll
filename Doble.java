package hotel;

public class Doble extends Habitacion {
    private int camasExtra;
    public Doble(int id, int numero, double precioBase, boolean disponible, int camasExtra) {
        super(id, numero, precioBase, disponible);
        this.camasExtra = camasExtra;
    }

    @Override
    public double calcularPrecio() {
        return getPrecioBase() + (camasExtra * 500);
    }
}
