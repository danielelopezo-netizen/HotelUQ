package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.exceptions.CancelacionInvalidaException;
import co.edu.uniquindio.hotel.exceptions.ReservaException;
import co.edu.uniquindio.hotel.model.Reserva;

public class EstadoCancelada implements EstadoReserva {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoEstadoReserva getTipo() {
        return TipoEstadoReserva.CANCELADA;
    }

    @Override
    public void confirmar(Reserva reserva) throws ReservaException {
        throw new ReservaException("No se puede confirmar una reserva previamente cancelada.");
    }

    @Override
    public void cancelar(Reserva reserva, boolean canceladoPorHotel) throws CancelacionInvalidaException {
        throw new CancelacionInvalidaException("La reserva ya ha sido cancelada.");
    }

    @Override
    public void completar(Reserva reserva) throws ReservaException {
        throw new ReservaException("No se puede completar una reserva cancelada.");
    }
}

