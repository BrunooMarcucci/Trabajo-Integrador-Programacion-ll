package hotel;

public class Suite extends Habitacion {
    private boolean incluyeDesayuno;
    public Suite(int id, int numero, double precioBase, boolean disponible, boolean incluyeDesayuno) {
        super(id, numero, precioBase, disponible);
        this.incluyeDesayuno = incluyeDesayuno;
    }

    @Override
    public double calcularPrecio() {
        return getPrecioBase() + (incluyeDesayuno ? 1000 : 0);
    }
}
