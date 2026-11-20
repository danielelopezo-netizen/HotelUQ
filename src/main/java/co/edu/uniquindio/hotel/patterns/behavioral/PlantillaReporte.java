package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.model.Reserva;
import co.edu.uniquindio.hotel.patterns.structural.DatosReporte;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PlantillaReporte {

    public final DatosReporte generar(List<Reserva> reservas, LocalDate fechaInicio, LocalDate fechaFin) {
        String periodo = (fechaInicio != null ? fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Inicio")
                + " al " + (fechaFin != null ? fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Actualidad");

        DatosReporte datos = new DatosReporte(getTitulo(), periodo);
        datos.setEncabezadosColumnas(getEncabezadosColumnas());

        List<Reserva> filtradas = filtrar(reservas, fechaInicio, fechaFin);
        procesarFilas(datos, filtradas);
        calcularTotales(datos, filtradas);

        return datos;
    }

    protected List<Reserva> filtrar(List<Reserva> reservas, LocalDate inicio, LocalDate fin) {
        return reservas.stream()
                .filter(r -> {
                    if (inicio != null && r.getFechaEntrada().isBefore(inicio)) return false;
                    if (fin != null && r.getFechaEntrada().isAfter(fin)) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    protected abstract String getTitulo();
    protected abstract List<String> getEncabezadosColumnas();
    protected abstract void procesarFilas(DatosReporte datos, List<Reserva> reservas);
    protected abstract void calcularTotales(DatosReporte datos, List<Reserva> reservas);
}
