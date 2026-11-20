package co.edu.uniquindio.hotel.patterns.structural;

import java.io.Serializable;

public interface ComponenteServicio extends Serializable {
    String getId();
    String getNombre();
    String getDescripcion();
    double getPrecio();
    boolean isPaquete();
}

