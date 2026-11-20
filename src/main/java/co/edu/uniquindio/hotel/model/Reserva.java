package co.edu.uniquindio.hotel.model;

import co.edu.uniquindio.hotel.patterns.behavioral.EstadoConfirmada;
import co.edu.uniquindio.hotel.patterns.behavioral.EstadoReserva;
import co.edu.uniquindio.hotel.patterns.behavioral.TipoEstadoReserva;
import co.edu.uniquindio.hotel.patterns.structural.ComponenteServicio;
import co.edu.uniquindio.hotel.patterns.structural.IDetalleReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Reserva implements IDetalleReserva {
    private static final long serialVersionUID = 1L;

    private String id;
    private String codigo;
    private Cliente cliente;
    private Habitacion habitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int numeroHuespedes;
    private final List<ComponenteServicio> servicios = new ArrayList<>();
    private Promocion promocionAplicada;
    private double descuentoPromocion;
    private double descuentoLargaEstadia;
    private double descuentoPuntos;
    private int puntosRedimidos;
    private double costoHospedajeBruto;
    private double costoServicios;
    private double totalNeto;
    private LocalDateTime fechaCreacion;
    private EstadoReserva estadoActual;
    private boolean fueReembolsado;
    private double montoReembolsado;
    private boolean tieneSeguroCancelacion;

    public Reserva() {
        this.fechaCreacion = LocalDateTime.now();
        this.estadoActual = new EstadoConfirmada();
    }

    public Reserva(String id, String codigo, Cliente cliente, Habitacion habitacion,
                   LocalDate fechaEntrada, LocalDate fechaSalida, int numeroHuespedes) {
        this.id = id;
        this.codigo = codigo;
        this.cliente = cliente;
        this.habitacion = habitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numeroHuespedes = numeroHuespedes;
        this.fechaCreacion = LocalDateTime.now();
        this.estadoActual = new EstadoConfirmada();
    }

    public int getNumeroNoches() {
        if (fechaEntrada == null || fechaSalida == null) return 0;
        return (int) Math.max(1, ChronoUnit.DAYS.between(fechaEntrada, fechaSalida));
    }

    public boolean seSuperponeCon(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return false;
        // Solo reservas activas (no canceladas) generan conflicto
        if (estadoActual != null && estadoActual.getTipo() == TipoEstadoReserva.CANCELADA) {
            return false;
        }
        return this.fechaEntrada.isBefore(fin) && this.fechaSalida.isAfter(inicio);
    }

    public void recalcularTotales() {
        int noches = getNumeroNoches();
        this.costoHospedajeBruto = habitacion != null ? habitacion.calcularTarifaBase(noches) : 0.0;
        this.costoServicios = servicios.stream().mapToDouble(ComponenteServicio::getPrecio).sum();

        // Regla Adicional 1: Descuento 10% por larga estadía (> 7 noches)
        if (noches > 7) {
            this.descuentoLargaEstadia = this.costoHospedajeBruto * 0.10;
        } else {
            this.descuentoLargaEstadia = 0.0;
        }

        // Promoción si aplica
        if (promocionAplicada != null && promocionAplicada.esAplicable(habitacion, noches, fechaEntrada)) {
            this.descuentoPromocion = promocionAplicada.calcularDescuento(this.costoHospedajeBruto);
        } else {
            this.descuentoPromocion = 0.0;
        }

        double totalSinPuntos = Math.max(0.0, (costoHospedajeBruto - descuentoLargaEstadia - descuentoPromocion) + costoServicios);
        this.totalNeto = Math.max(0.0, totalSinPuntos - descuentoPuntos);
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public double getPrecioFinal() {
        return totalNeto;
    }

    @Override
    public String getDetalleAdicional() {
        return "Reserva " + codigo + " [" + (habitacion != null ? habitacion.getNumero() : "") + "]";
    }

    @Override
    public boolean tieneSeguroCancelacionFlexible() {
        return tieneSeguroCancelacion;
    }

    public void setTieneSeguroCancelacion(boolean tieneSeguroCancelacion) {
        this.tieneSeguroCancelacion = tieneSeguroCancelacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public int getNumeroHuespedes() {
        return numeroHuespedes;
    }

    public void setNumeroHuespedes(int numeroHuespedes) {
        this.numeroHuespedes = numeroHuespedes;
    }

    public List<ComponenteServicio> getServicios() {
        return Collections.unmodifiableList(servicios);
    }

    public void agregarServicio(ComponenteServicio servicio) {
        if (servicio != null) {
            this.servicios.add(servicio);
            recalcularTotales();
        }
    }

    public void limpiarServicios() {
        this.servicios.clear();
        recalcularTotales();
    }

    public Promocion getPromocionAplicada() {
        return promocionAplicada;
    }

    public void setPromocionAplicada(Promocion promocionAplicada) {
        this.promocionAplicada = promocionAplicada;
        recalcularTotales();
    }

    public double getDescuentoPromocion() {
        return descuentoPromocion;
    }

    public double getDescuentoLargaEstadia() {
        return descuentoLargaEstadia;
    }

    public double getDescuentoPuntos() {
        return descuentoPuntos;
    }

    public void setDescuentoPuntos(double descuentoPuntos, int puntosRedimidos) {
        this.descuentoPuntos = descuentoPuntos;
        this.puntosRedimidos = puntosRedimidos;
        recalcularTotales();
    }

    public int getPuntosRedimidos() {
        return puntosRedimidos;
    }

    public double getCostoHospedajeBruto() {
        return costoHospedajeBruto;
    }

    public double getCostoServicios() {
        return costoServicios;
    }

    public double getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(double totalNeto) {
        this.totalNeto = totalNeto;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoReserva getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoReserva estadoActual) {
        this.estadoActual = estadoActual;
    }

    public TipoEstadoReserva getTipoEstado() {
        return estadoActual != null ? estadoActual.getTipo() : TipoEstadoReserva.PENDIENTE;
    }

    public boolean isFueReembolsado() {
        return fueReembolsado;
    }

    public void setFueReembolsado(boolean fueReembolsado) {
        this.fueReembolsado = fueReembolsado;
    }

    public double getMontoReembolsado() {
        return montoReembolsado;
    }

    public void setMontoReembolsado(double montoReembolsado) {
        this.montoReembolsado = montoReembolsado;
    }

    public String getFechaEntradaFormateada() {
        return fechaEntrada != null ? fechaEntrada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getFechaSalidaFormateada() {
        return fechaSalida != null ? fechaSalida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva reserva)) return false;
        return Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return codigo + " - " + (cliente != null ? cliente.getNombre() : "") + " (" + getFechaEntradaFormateada() + " al " + getFechaSalidaFormateada() + ") [" + getTipoEstado() + "]";
    }
}
