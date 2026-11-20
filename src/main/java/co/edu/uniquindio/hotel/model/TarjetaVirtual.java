package co.edu.uniquindio.hotel.model;

import co.edu.uniquindio.hotel.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.hotel.exceptions.TarjetaInactivaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaVirtual implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numeroTarjeta;
    private double saldo;
    private boolean activa;
    private final List<MovimientoTarjeta> movimientos;

    public TarjetaVirtual() {
        this.numeroTarjeta = "TV-" + System.currentTimeMillis() % 100000;
        this.saldo = 0.0;
        this.activa = true;
        this.movimientos = new ArrayList<>();
    }

    public TarjetaVirtual(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
        this.saldo = 0.0;
        this.activa = true;
        this.movimientos = new ArrayList<>();
    }

    public synchronized void recargar(double monto, String detalle) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser positivo");
        }
        this.saldo += monto;
        MovimientoTarjeta mov = new MovimientoTarjeta(TipoMovimiento.RECARGA, monto, detalle, this.saldo);
        this.movimientos.add(mov);
    }

    public synchronized void debitar(double monto, String concepto) throws TarjetaInactivaException, SaldoInsuficienteException {
        if (!activa) {
            throw new TarjetaInactivaException("La tarjeta virtual está inactiva o bloqueada.");
        }
        if (monto <= 0) {
            return;
        }
        if (this.saldo < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente en tarjeta virtual. Saldo actual: $" 
                    + String.format("%,.0f", saldo) + " | Monto requerido: $" + String.format("%,.0f", monto));
        }
        this.saldo -= monto;
        MovimientoTarjeta mov = new MovimientoTarjeta(TipoMovimiento.PAGO_RESERVA, -monto, concepto, this.saldo);
        this.movimientos.add(mov);
    }

    public synchronized void reembolsar(double monto, String motivo) {
        if (monto <= 0) return;
        this.saldo += monto;
        MovimientoTarjeta mov = new MovimientoTarjeta(TipoMovimiento.REEMBOLSO_CANCELACION, monto, motivo, this.saldo);
        this.movimientos.add(mov);
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<MovimientoTarjeta> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }
}
