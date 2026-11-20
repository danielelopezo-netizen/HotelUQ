package co.edu.uniquindio.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Promocion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String codigo;
    private String nombre;
    private double porcentajeDescuento;
    private int nochesMinimas;
    private TipoHabitacion tipoHabitacionRequerida; // null si aplica a cualquier tipo
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;

    public Promocion() {
        this.activa = true;
    }

    public Promocion(String id, String codigo, String nombre, double porcentajeDescuento, int nochesMinimas,
                     TipoHabitacion tipoHabitacionRequerida, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
        this.nochesMinimas = nochesMinimas;
        this.tipoHabitacionRequerida = tipoHabitacionRequerida;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = true;
    }

    public boolean esAplicable(Habitacion habitacion, int numeroNoches, LocalDate fechaEntrada) {
        if (!activa) return false;
        if (numeroNoches < nochesMinimas) return false;
        if (tipoHabitacionRequerida != null && habitacion != null && habitacion.getTipo() != tipoHabitacionRequerida) {
            return false;
        }
        if (fechaInicio != null && fechaEntrada.isBefore(fechaInicio)) return false;
        if (fechaFin != null && fechaEntrada.isAfter(fechaFin)) return false;
        return true;
    }

    public double calcularDescuento(double subtotalHospedaje) {
        if (subtotalHospedaje <= 0) return 0.0;
        return (subtotalHospedaje * porcentajeDescuento) / 100.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public int getNochesMinimas() {
        return nochesMinimas;
    }

    public void setNochesMinimas(int nochesMinimas) {
        this.nochesMinimas = nochesMinimas;
    }

    public TipoHabitacion getTipoHabitacionRequerida() {
        return tipoHabitacionRequerida;
    }

    public void setTipoHabitacionRequerida(TipoHabitacion tipoHabitacionRequerida) {
        this.tipoHabitacionRequerida = tipoHabitacionRequerida;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Promocion promocion)) return false;
        return Objects.equals(id, promocion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + porcentajeDescuento + "% dcto)";
    }
}
