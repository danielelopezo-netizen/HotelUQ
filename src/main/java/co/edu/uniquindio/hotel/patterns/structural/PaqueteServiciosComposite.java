package co.edu.uniquindio.hotel.patterns.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PaqueteServiciosComposite implements ComponenteServicio {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String descripcion;
    private double porcentajeDescuento; // ej. 15.0 para 15%
    private final List<ComponenteServicio> componentes;

    public PaqueteServiciosComposite() {
        this.componentes = new ArrayList<>();
    }

    public PaqueteServiciosComposite(String id, String nombre, String descripcion, double porcentajeDescuento) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.porcentajeDescuento = porcentajeDescuento;
        this.componentes = new ArrayList<>();
    }

    public void agregarComponente(ComponenteServicio componente) {
        if (componente != null && !componentes.contains(componente)) {
            componentes.add(componente);
        }
    }

    public void removerComponente(ComponenteServicio componente) {
        componentes.remove(componente);
    }

    public List<ComponenteServicio> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }

    public double getSubtotalSinDescuento() {
        return componentes.stream().mapToDouble(ComponenteServicio::getPrecio).sum();
    }

    @Override
    public double getPrecio() {
        double subtotal = getSubtotalSinDescuento();
        double factor = (100.0 - porcentajeDescuento) / 100.0;
        return Math.max(0.0, subtotal * factor);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public boolean isPaquete() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaqueteServiciosComposite that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "📦 " + nombre + " (" + porcentajeDescuento + "% off) - $" + String.format("%,.0f", getPrecio());
    }
}
