package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.model.Reserva;
import co.edu.uniquindio.hotel.patterns.structural.DatosReporte;

import java.util.Arrays;
import java.util.List;

public class ReporteOcupacionTemplate extends PlantillaReporte {

    @Override
    protected String getTitulo() {
        return "Reporte Operativo de Ocupación de Habitaciones";
    }

    @Override
    protected List<String> getEncabezadosColumnas() {
        return Arrays.asList("Código", "Habitación", "Tipo", "Huéspedes", "Noches", "Fechas", "Estado");
    }

    @Override
    protected void procesarFilas(DatosReporte datos, List<Reserva> reservas) {
        for (Reserva r : reservas) {
            datos.agregarFila(
                    r.getCodigo(),
                    r.getHabitacion().getNumero(),
                    r.getHabitacion().getTipo().getNombreVisual(),
                    String.valueOf(r.getNumeroHuespedes()),
                    String.valueOf(r.getNumeroNoches()),
                    r.getFechaEntradaFormateada() + " a " + r.getFechaSalidaFormateada(),
                    r.getTipoEstado().getNombreVisual()
            );
        }
    }

    @Override
    protected void calcularTotales(DatosReporte datos, List<Reserva> reservas) {
        long totalReservas = reservas.size();
        long confirmadas = reservas.stream().filter(r -> r.getTipoEstado() == TipoEstadoReserva.CONFIRMADA).count();
        long completadas = reservas.stream().filter(r -> r.getTipoEstado() == TipoEstadoReserva.COMPLETADA).count();
        long canceladas = reservas.stream().filter(r -> r.getTipoEstado() == TipoEstadoReserva.CANCELADA).count();

        datos.agregarTotal("Total de Reservas Evaluadas", String.valueOf(totalReservas));
        datos.agregarTotal("Reservas Activas / Confirmadas", String.valueOf(confirmadas));
        datos.agregarTotal("Estadías Finalizadas", String.valueOf(completadas));
        datos.agregarTotal("Reservas Canceladas", String.valueOf(canceladas));
    }
}
