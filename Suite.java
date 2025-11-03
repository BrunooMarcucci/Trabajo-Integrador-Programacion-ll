package hotel;

public class Suite extends Habitacion {

    boolean incluyeDesayuno;

    public Suite(int id, int numero, double precioBase, boolean disponible, boolean incluyeDesayuno) {
        super(id, numero, precioBase, disponible);
        this.incluyeDesayuno = incluyeDesayuno;
    }

    @Override
    public double calcularPrecio() {
        return incluyeDesayuno ? getPrecioBase() + 500 : getPrecioBase();
    }
        
    // Sobrecarga — calcula precio con días
    public double calcularPrecio(int dias) {
        double precio = incluyeDesayuno ? getPrecioBase() + 500 : getPrecioBase();
        return precio * dias;
    }
}
