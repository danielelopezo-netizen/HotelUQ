package co.edu.uniquindio.hotel.model;

import co.edu.uniquindio.hotel.exceptions.PuntosInsuficientesException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuentaPuntos implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double VALOR_POR_PUNTO_COP = 100.0; // 1 punto = $100 COP de descuento
    public static final double MONTO_BASE_ACUMULACION_COP = 100000.0; // Cada $100.000 COP
    public static final int PUNTOS_POR_BASE = 100; // Otorgan 100 puntos (1 punto por cada $1000)

    private String clienteIdentificacion;
    private final List<RegistroPuntos> registros;

    public CuentaPuntos() {
        this.registros = new ArrayList<>();
    }

    public CuentaPuntos(String clienteIdentificacion) {
        this.clienteIdentificacion = clienteIdentificacion;
        this.registros = new ArrayList<>();
    }

    public synchronized int acumularPorPago(double montoPagado, LocalDate fechaPago, String detalle) {
        if (montoPagado <= 0) return 0;
        int puntosGanados = (int) ((montoPagado / MONTO_BASE_ACUMULACION_COP) * PUNTOS_POR_BASE);
        if (puntosGanados > 0) {
            RegistroPuntos nuevo = new RegistroPuntos(puntosGanados, fechaPago != null ? fechaPago : LocalDate.now(), detalle);
            registros.add(nuevo);
        }
        return puntosGanados;
    }

    public synchronized void acreditarPuntosDirectos(int puntos, LocalDate fecha, String motivo) {
        if (puntos > 0) {
            registros.add(new RegistroPuntos(puntos, fecha != null ? fecha : LocalDate.now(), motivo));
        }
    }

    public synchronized int getPuntosValidos(LocalDate fechaReferencia) {
        LocalDate hoy = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        return registros.stream()
                .filter(r -> !r.estaVencido(hoy))
                .mapToInt(RegistroPuntos::getPuntosDisponibles)
                .sum();
    }

    public synchronized int getPuntosVencidos(LocalDate fechaReferencia) {
        LocalDate hoy = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        return registros.stream()
                .filter(r -> r.estaVencido(hoy))
                .mapToInt(RegistroPuntos::getPuntosDisponibles)
                .sum();
    }

    /**
     * Redime puntos utilizando la política estricta FIFO:
     * consume primero los lotes no vencidos cuya fecha de vencimiento sea más próxima.
     */
    public synchronized void redimirPuntos(int puntosARedimir, LocalDate fechaReferencia) throws PuntosInsuficientesException {
        if (puntosARedimir <= 0) return;
        LocalDate hoy = fechaReferencia != null ? fechaReferencia : LocalDate.now();

        int puntosDisponibles = getPuntosValidos(hoy);
        if (puntosDisponibles < puntosARedimir) {
            throw new PuntosInsuficientesException("Puntos insuficientes o vencidos. Puntos válidos: " 
                    + puntosDisponibles + " | Requeridos: " + puntosARedimir);
        }

        // Ordenar registros por fecha de vencimiento (más cercanos primero)
        List<RegistroPuntos> validos = new ArrayList<>(registros.stream()
                .filter(r -> !r.estaVencido(hoy) && r.getPuntosDisponibles() > 0)
                .sorted()
                .toList());

        int faltantes = puntosARedimir;
        for (RegistroPuntos reg : validos) {
            int consumidos = reg.consumirPuntos(faltantes);
            faltantes -= consumidos;
            if (faltantes <= 0) {
                break;
            }
        }
    }

    public List<RegistroPuntos> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    public String getClienteIdentificacion() {
        return clienteIdentificacion;
    }

    public void setClienteIdentificacion(String clienteIdentificacion) {
        this.clienteIdentificacion = clienteIdentificacion;
    }
}

