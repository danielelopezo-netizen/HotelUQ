package co.edu.uniquindio.hotel.patterns.behavioral;

public enum TipoEstadoReserva {
    PENDIENTE("Pendiente de Pago"),
    CONFIRMADA("Confirmada y Pagada"),
    CANCELADA("Cancelada"),
    COMPLETADA("Estadía Completada");

    private final String nombreVisual;

    TipoEstadoReserva(String nombreVisual) {
        this.nombreVisual = nombreVisual;
    }

    public String getNombreVisual() {
        return nombreVisual;
    }

    @Override
    public String toString() {
        return nombreVisual;
    }
}
