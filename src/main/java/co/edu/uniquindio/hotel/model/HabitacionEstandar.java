package co.edu.uniquindio.hotel.model;

public class HabitacionEstandar extends Habitacion {

    public HabitacionEstandar(String numero, int piso, double precioBaseNoche, String descripcion) {
        super(numero, piso, TipoHabitacion.ESTANDAR, precioBaseNoche, descripcion);
    }

    @Override
    public double calcularTarifaBase(int numeroNoches) {
        return precioBaseNoche * numeroNoches;
    }
}

