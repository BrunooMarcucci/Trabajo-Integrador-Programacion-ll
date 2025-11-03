package hotel;

public class SuitePresidencial extends Habitacion implements BeneficioAdicional {

    private boolean desayuno;
    private boolean servicioVip;

    public SuitePresidencial(int id, int numero, double precioBase, boolean disponible, boolean desayuno, boolean servicioVip) {
        super(id, numero, precioBase, disponible);
        this.desayuno = desayuno;
        this.servicioVip = servicioVip;
    }

    @Override
    public double calcularPrecio() {
        double extra = 0;
        if (desayuno) extra += 500;
        if (servicioVip) extra += 1000;
        return precioBase + extra;
    }

    @Override
    public double aplicarBeneficio(double precioBase) {
        // En este caso, el beneficio puede ser un "cargo extra" (por lujo)
        return precioBase + 2000;
    }
}


