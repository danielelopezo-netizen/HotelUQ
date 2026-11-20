package co.edu.uniquindio.hotel.model;

public class HabitacionSuite extends Habitacion {

    public HabitacionSuite(String numero, int piso, double precioBaseNoche, String descripcion) {
        super(numero, piso, TipoHabitacion.SUITE, precioBaseNoche, descripcion);
    }

    @Override
    public double calcularTarifaBase(int numeroNoches) {
        return precioBaseNoche * numeroNoches;
    }
}

