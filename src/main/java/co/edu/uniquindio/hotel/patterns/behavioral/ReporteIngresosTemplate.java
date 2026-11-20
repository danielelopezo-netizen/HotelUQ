package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.model.Reserva;
import co.edu.uniquindio.hotel.patterns.structural.DatosReporte;

import java.util.Arrays;
import java.util.List;

public class ReporteIngresosTemplate extends PlantillaReporte {

    @Override
    protected String getTitulo() {
        return "Reporte Financiero de Ingresos y Reembolsos";
    }

    @Override
    protected List<String> getEncabezadosColumnas() {
        return Arrays.asList("Código", "Cliente", "Habitación", "Entrada", "Salida", "Hospedaje", "Servicios", "Total Neto", "Estado");
    }

    @Override
    protected void procesarFilas(DatosReporte datos, List<Reserva> reservas) {
        for (Reserva r : reservas) {
            datos.agregarFila(
                    r.getCodigo(),
                    r.getCliente().getNombre(),
                    r.getHabitacion().getNumero() + " (" + r.getHabitacion().getTipo() + ")",
                    r.getFechaEntradaFormateada(),
                    r.getFechaSalidaFormateada(),
                    "$" + String.format("%,.0f", r.getCostoHospedajeBruto()),
                    "$" + String.format("%,.0f", r.getCostoServicios()),
                    "$" + String.format("%,.0f", r.getTotalNeto()),
                    r.getTipoEstado().getNombreVisual()
            );
        }
    }

    @Override
    protected void calcularTotales(DatosReporte datos, List<Reserva> reservas) {
        double totalIngresos = reservas.stream()
                .filter(r -> r.getTipoEstado() != TipoEstadoReserva.CANCELADA)
                .mapToDouble(Reserva::getTotalNeto)
                .sum();

        double totalReembolsos = reservas.stream()
                .filter(Reserva::isFueReembolsado)
                .mapToDouble(Reserva::getMontoReembolsado)
                .sum();

        datos.agregarTotal("Ingresos Brutos Confirmados", "$" + String.format("%,.0f", totalIngresos));
        datos.agregarTotal("Reembolsos Efectuados", "$" + String.format("%,.0f", totalReembolsos));
        datos.agregarTotal("Ingresos Netos Totales", "$" + String.format("%,.0f", totalIngresos - totalReembolsos));
    }
}
