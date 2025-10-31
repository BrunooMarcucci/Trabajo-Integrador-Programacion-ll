package hotel;

public class HuespedVIP extends Huesped implements Reservable {
    private final double descuento = 0.3; // 30% fijo

    public HuespedVIP(int id, String nombre, String dni) {
        super(id, nombre, dni);
    }

    @Override
    public void reservar() {
        System.out.println(getNombre() + " (VIP) realizará una reserva con descuento del " + (int)(descuento*100) + "%");
    }

    public double getDescuento() { return descuento; }
}
