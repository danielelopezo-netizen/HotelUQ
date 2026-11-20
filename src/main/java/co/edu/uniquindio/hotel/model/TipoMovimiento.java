package co.edu.uniquindio.hotel.model;

public enum TipoMovimiento {
    RECARGA("Recarga autorizada por Administrador"),
    PAGO_RESERVA("Pago de reserva y servicios"),
    REEMBOLSO_CANCELACION("Reembolso por cancelación de reserva"),
    AJUSTE("Ajuste contable administrativo");

    private final String descripcion;

    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

