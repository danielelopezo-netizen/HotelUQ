package co.edu.uniquindio.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RegistroPuntos implements Serializable, Comparable<RegistroPuntos> {
    private static final long serialVersionUID = 1L;

    private String id;
    private int puntosOriginales;
    private int puntosDisponibles;
    private LocalDate fechaObtencion;
    private LocalDate fechaVencimiento;
    private String concepto;

    public RegistroPuntos() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
    }

    public RegistroPuntos(int puntos, LocalDate fechaObtencion, String concepto) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.puntosOriginales = puntos;
        this.puntosDisponibles = puntos;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
        this.concepto = concepto;
    }

    public boolean estaVencido(LocalDate fechaReferencia) {
        return fechaReferencia.isAfter(fechaVencimiento);
    }

    public int consumirPuntos(int cantidadRequerida) {
        if (cantidadRequerida <= 0 || puntosDisponibles <= 0) return 0;
        int aConsumir = Math.min(cantidadRequerida, puntosDisponibles);
        puntosDisponibles -= aConsumir;
        return aConsumir;
    }

    @Override
    public int compareTo(RegistroPuntos otro) {
        // Orden FIFO: Primero los que vencen antes
        return this.fechaVencimiento.compareTo(otro.fechaVencimiento);
    }

    public String getId() {
        return id;
    }

    public int getPuntosOriginales() {
        return puntosOriginales;
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getFechaObtencionFormateada() {
        return fechaObtencion != null ? fechaObtencion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getFechaVencimientoFormateada() {
        return fechaVencimiento != null ? fechaVencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    @Override
    public String toString() {
        return puntosDisponibles + "/" + puntosOriginales + " pts (Vence: " + getFechaVencimientoFormateada() + ") - " + concepto;
    }
}

