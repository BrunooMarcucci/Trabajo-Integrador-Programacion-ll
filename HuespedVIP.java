package hotel;

public class HuespedVIP extends Huesped implements BeneficioAdicional {

    private double descuento = 0.3; // 30%

    public HuespedVIP(int id, String nombre, String dni) {
        super(id, nombre, dni);
    }

    public double getDescuento() {
        return descuento;
    }

    @Override
    public double aplicarBeneficio(double precioBase) {
        return precioBase * (1 - descuento);
    }
}

