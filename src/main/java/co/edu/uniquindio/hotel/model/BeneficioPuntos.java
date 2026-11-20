package co.edu.uniquindio.hotel.model;

import java.io.Serializable;

public class BeneficioPuntos implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String descripcion;
    private int puntosRequeridos;
    private double valorEquivalenteCOP;
    private TipoBeneficio tipo;

    public enum TipoBeneficio {
        DESCUENTO_RESERVA,
        SERVICIO_GRATIS,
        PAQUETE_SERVICIOS
    }

    public BeneficioPuntos() {
    }

    public BeneficioPuntos(String id, String nombre, String descripcion, int puntosRequeridos, double valorEquivalenteCOP, TipoBeneficio tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosRequeridos = puntosRequeridos;
        this.valorEquivalenteCOP = valorEquivalenteCOP;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public double getValorEquivalenteCOP() {
        return valorEquivalenteCOP;
    }

    public TipoBeneficio getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nombre + " (" + puntosRequeridos + " pts) - Equiv: $" + String.format("%,.0f", valorEquivalenteCOP);
    }
}
