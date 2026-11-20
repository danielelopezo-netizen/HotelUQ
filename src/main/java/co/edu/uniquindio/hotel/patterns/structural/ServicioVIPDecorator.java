package co.edu.uniquindio.hotel.patterns.structural;

public class ServicioVIPDecorator extends DecoradorReserva {
    private static final long serialVersionUID = 1L;
    public static final double COSTO_VIP = 50000.0;

    public ServicioVIPDecorator(IDetalleReserva reservaEnvuelto) {
        super(reservaEnvuelto);
    }

    @Override
    public double getPrecioFinal() {
        return super.getPrecioFinal() + COSTO_VIP;
    }

    @Override
    public String getDetalleAdicional() {
        return super.getDetalleAdicional() + " + [👑 Experiencia VIP: Check-in express y coctel de bienvenida]";
    }
}

