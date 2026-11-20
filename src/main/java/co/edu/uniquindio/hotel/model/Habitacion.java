package co.edu.uniquindio.hotel.model;

import java.io.Serializable;
import java.util.Objects;

public abstract class Habitacion implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String numero;
    protected int piso;
    protected TipoHabitacion tipo;
    protected double precioBaseNoche;
    protected String descripcion;
    protected boolean activa;

    public Habitacion() {
        this.activa = true;
    }

    public Habitacion(String numero, int piso, TipoHabitacion tipo, double precioBaseNoche, String descripcion) {
        this.numero = numero;
        this.piso = piso;
        this.tipo = tipo;
        this.precioBaseNoche = precioBaseNoche;
        this.descripcion = descripcion;
        this.activa = true;
    }

    public abstract double calcularTarifaBase(int numeroNoches);

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public double getPrecioBaseNoche() {
        return precioBaseNoche;
    }

    public void setPrecioBaseNoche(double precioBaseNoche) {
        this.precioBaseNoche = precioBaseNoche;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getCapacidadMaxima() {
        return tipo != null ? tipo.getCapacidadMaxima() : 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Habitacion that)) return false;
        return Objects.equals(numero, that.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return "Habitación " + numero + " (" + (tipo != null ? tipo.getNombreVisual() : "") + ") - $" + String.format("%,.0f", precioBaseNoche) + "/noche";
    }
}
