package co.edu.uniquindio.hotel.patterns.structural;

public class SeguroCancelacionFlexibleDecorator extends DecoradorReserva {
    private static final long serialVersionUID = 1L;
    public static final double COSTO_SEGURO = 35000.0;

    public SeguroCancelacionFlexibleDecorator(IDetalleReserva reservaEnvuelto) {
        super(reservaEnvuelto);
    }

    @Override
    public double getPrecioFinal() {
        return super.getPrecioFinal() + COSTO_SEGURO;
    }

    @Override
    public String getDetalleAdicional() {
        return super.getDetalleAdicional() + " + [🛡️ Seguro de Cancelación 100% Flexible]";
    }

    @Override
    public boolean tieneSeguroCancelacionFlexible() {
        return true;
    }
}
