package co.edu.uniquindio.hotel.patterns.structural;

public abstract class DecoradorReserva implements IDetalleReserva {
    private static final long serialVersionUID = 1L;

    protected final IDetalleReserva reservaEnvuelto;

    public DecoradorReserva(IDetalleReserva reservaEnvuelto) {
        this.reservaEnvuelto = reservaEnvuelto;
    }

    @Override
    public String getCodigo() {
        return reservaEnvuelto.getCodigo();
    }

    @Override
    public double getPrecioFinal() {
        return reservaEnvuelto.getPrecioFinal();
    }

    @Override
    public String getDetalleAdicional() {
        return reservaEnvuelto.getDetalleAdicional();
    }

    @Override
    public boolean tieneSeguroCancelacionFlexible() {
        return reservaEnvuelto.tieneSeguroCancelacionFlexible();
    }
}
