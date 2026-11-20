package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.exceptions.CancelacionInvalidaException;
import co.edu.uniquindio.hotel.exceptions.ReservaException;
import co.edu.uniquindio.hotel.model.Reserva;

public class EstadoPendiente implements EstadoReserva {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoEstadoReserva getTipo() {
        return TipoEstadoReserva.PENDIENTE;
    }

    @Override
    public void confirmar(Reserva reserva) throws ReservaException {
        reserva.setEstadoActual(new EstadoConfirmada());
    }

    @Override
    public void cancelar(Reserva reserva, boolean canceladoPorHotel) throws CancelacionInvalidaException {
        reserva.setEstadoActual(new EstadoCancelada());
    }

    @Override
    public void completar(Reserva reserva) throws ReservaException {
        throw new ReservaException("No se puede completar una reserva que no ha sido pagada ni confirmada.");
    }
}
