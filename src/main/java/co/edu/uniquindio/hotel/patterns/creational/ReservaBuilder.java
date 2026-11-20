package co.edu.uniquindio.hotel.patterns.creational;

import co.edu.uniquindio.hotel.exceptions.ReservaException;
import co.edu.uniquindio.hotel.model.Cliente;
import co.edu.uniquindio.hotel.model.Habitacion;
import co.edu.uniquindio.hotel.model.Promocion;
import co.edu.uniquindio.hotel.model.Reserva;
import co.edu.uniquindio.hotel.patterns.structural.ComponenteServicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservaBuilder {
    private String id;
    private String codigo;
    private Cliente cliente;
    private Habitacion habitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int numeroHuespedes = 1;
    private final List<ComponenteServicio> servicios = new ArrayList<>();
    private Promocion promocion;
    private double descuentoPuntos = 0.0;
    private int puntosRedimidos = 0;
    private boolean tieneSeguroCancelacion = false;

    public ReservaBuilder() {
        this.id = UUID.randomUUID().toString();
        this.codigo = "RES-" + (System.currentTimeMillis() % 1000000);
    }

    public ReservaBuilder conIdYCodigo(String id, String codigo) {
        this.id = id;
        this.codigo = codigo;
        return this;
    }

    public ReservaBuilder paraCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public ReservaBuilder enHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
        return this;
    }

    public ReservaBuilder conFechas(LocalDate entrada, LocalDate salida) {
        this.fechaEntrada = entrada;
        this.fechaSalida = salida;
        return this;
    }

    public ReservaBuilder conHuespedes(int huespedes) {
        this.numeroHuespedes = huespedes;
        return this;
    }

    public ReservaBuilder agregarServicio(ComponenteServicio servicio) {
        if (servicio != null) {
            this.servicios.add(servicio);
        }
        return this;
    }

    public ReservaBuilder conServicios(List<ComponenteServicio> listaServicios) {
        if (listaServicios != null) {
            this.servicios.clear();
            this.servicios.addAll(listaServicios);
        }
        return this;
    }

    public ReservaBuilder aplicarPromocion(Promocion promocion) {
        this.promocion = promocion;
        return this;
    }

    public ReservaBuilder redimirPuntos(int puntos, double descuentoEquivalente) {
        this.puntosRedimidos = puntos;
        this.descuentoPuntos = descuentoEquivalente;
        return this;
    }

    public ReservaBuilder conSeguroCancelacion(boolean activo) {
        this.tieneSeguroCancelacion = activo;
        return this;
    }

    public Reserva build() throws ReservaException {
        // Validaciones estrictas de negocio (Reglas 3.1 y adicionales)
        if (cliente == null) {
            throw new ReservaException("La reserva debe tener un cliente asignado.");
        }
        if (habitacion == null) {
            throw new ReservaException("Debe seleccionar una habitación para la reserva.");
        }
        if (fechaEntrada == null || fechaSalida == null) {
            throw new ReservaException("Las fechas de entrada y salida son obligatorias.");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new ReservaException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
        if (numeroHuespedes <= 0) {
            throw new ReservaException("El número de huéspedes debe ser al menos 1.");
        }
        // Regla Adicional 2: Control estricto de capacidad máxima
        if (numeroHuespedes > habitacion.getCapacidadMaxima()) {
            throw new ReservaException("La habitación " + habitacion.getNumero() + " (" + habitacion.getTipo() 
                    + ") permite máximo " + habitacion.getCapacidadMaxima() + " personas. (Solicitado: " + numeroHuespedes + ")");
        }

        Reserva reserva = new Reserva(id, codigo, cliente, habitacion, fechaEntrada, fechaSalida, numeroHuespedes);
        reserva.setTieneSeguroCancelacion(tieneSeguroCancelacion);
        for (ComponenteServicio s : servicios) {
            reserva.agregarServicio(s);
        }
        if (promocion != null) {
            reserva.setPromocionAplicada(promocion);
        }
        if (puntosRedimidos > 0) {
            reserva.setDescuentoPuntos(descuentoPuntos, puntosRedimidos);
        }
        reserva.recalcularTotales();
        return reserva;
    }
}

