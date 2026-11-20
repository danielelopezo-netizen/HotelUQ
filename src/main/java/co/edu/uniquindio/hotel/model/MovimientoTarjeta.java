package co.edu.uniquindio.hotel.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MovimientoTarjeta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime fechaHora;
    private TipoMovimiento tipo;
    private double monto;
    private String descripcion;
    private double saldoPosterior;

    public MovimientoTarjeta() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.fechaHora = LocalDateTime.now();
    }

    public MovimientoTarjeta(TipoMovimiento tipo, double monto, String descripcion, double saldoPosterior) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.fechaHora = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.saldoPosterior = saldoPosterior;
    }

    public MovimientoTarjeta(LocalDateTime fechaHora, TipoMovimiento tipo, double monto, String descripcion, double saldoPosterior) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.saldoPosterior = saldoPosterior;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getFechaHoraFormateada() {
        return fechaHora != null ? fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getSaldoPosterior() {
        return saldoPosterior;
    }

    @Override
    public String toString() {
        return "[" + getFechaHoraFormateada() + "] " + tipo + ": $" + String.format("%,.0f", monto) + " | Saldo: $" + String.format("%,.0f", saldoPosterior);
    }
}
