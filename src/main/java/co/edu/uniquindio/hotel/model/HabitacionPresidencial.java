package co.edu.uniquindio.hotel.model;

public class HabitacionPresidencial extends Habitacion {

    public HabitacionPresidencial(String numero, int piso, double precioBaseNoche, String descripcion) {
        super(numero, piso, TipoHabitacion.PRESIDENCIAL, precioBaseNoche, descripcion);
    }

    @Override
    public double calcularTarifaBase(int numeroNoches) {
        return precioBaseNoche * numeroNoches;
    }
}

