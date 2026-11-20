package co.edu.uniquindio.hotel.patterns.creational;

import co.edu.uniquindio.hotel.model.*;

public class HabitacionFactory {

    public static Habitacion crearHabitacion(TipoHabitacion tipo, String numero, int piso, double precioBaseNoche, String descripcion) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de habitación no puede ser nulo.");
        }

        switch (tipo) {
            case ESTANDAR:
                return new HabitacionEstandar(numero, piso, precioBaseNoche, descripcion);
            case FAMILIAR:
                return new HabitacionFamiliar(numero, piso, precioBaseNoche, descripcion);
            case SUITE:
                return new HabitacionSuite(numero, piso, precioBaseNoche, descripcion);
            case PRESIDENCIAL:
                return new HabitacionPresidencial(numero, piso, precioBaseNoche, descripcion);
            default:
                throw new UnsupportedOperationException("Tipo de habitación no soportado: " + tipo);
        }
    }
}
