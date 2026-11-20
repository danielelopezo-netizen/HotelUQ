package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.model.Reserva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistroAuditoriaObserver implements IReservaObserver {
    private static final long serialVersionUID = 1L;

    private final List<String> eventosAuditoria = new ArrayList<>();

    @Override
    public void onReservaCreada(Reserva reserva) {
        String evento = String.format("[%s] CREACIÓN: Reserva %s confirmada para %s. Hab: %s. Total: $%,.0f",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                reserva.getCodigo(),
                reserva.getCliente().getNombre(),
                reserva.getHabitacion().getNumero(),
                reserva.getTotalNeto());
        eventosAuditoria.add(evento);
    }

    @Override
    public void onReservaCancelada(Reserva reserva, double montoReembolso) {
        String evento = String.format("[%s] CANCELACIÓN: Reserva %s cancelada. Reembolso a tarjeta: $%,.0f",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                reserva.getCodigo(),
                montoReembolso);
        eventosAuditoria.add(evento);
    }

    @Override
    public void onReservaCompletada(Reserva reserva) {
        String evento = String.format("[%s] CHECK-OUT: Reserva %s finalizada exitosamente.",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                reserva.getCodigo());
        eventosAuditoria.add(evento);
    }

    public List<String> getEventosAuditoria() {
        return Collections.unmodifiableList(eventosAuditoria);
    }
}
