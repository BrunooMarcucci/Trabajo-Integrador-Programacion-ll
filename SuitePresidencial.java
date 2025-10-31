package hotel;

public class SuitePresidencial extends Suite {
    private boolean servicioVIP;

    public SuitePresidencial(int id, int numero, double precioBase, boolean disponible, boolean incluyeDesayuno, boolean servicioVIP) {
        super(id, numero, precioBase, disponible, incluyeDesayuno);
        this.servicioVIP = servicioVIP;
    }

    @Override
    public double calcularPrecio() {
        double precio = super.calcularPrecio();
        if (servicioVIP) {
            precio += 3000; // recargo por servicio VIP 
        }
        return precio;
    }
}

