package co.edu.uniquindio.hotel.model;

public class HabitacionFamiliar extends Habitacion {

    public HabitacionFamiliar(String numero, int piso, double precioBaseNoche, String descripcion) {
        super(numero, piso, TipoHabitacion.FAMILIAR, precioBaseNoche, descripcion);
    }

    @Override
    public double calcularTarifaBase(int numeroNoches) {
        return precioBaseNoche * numeroNoches;
    }
}
