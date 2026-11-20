package co.edu.uniquindio.hotel.patterns.structural;

import java.io.Serializable;

public interface IDetalleReserva extends Serializable {
    String getCodigo();
    double getPrecioFinal();
    String getDetalleAdicional();
    boolean tieneSeguroCancelacionFlexible();
}

