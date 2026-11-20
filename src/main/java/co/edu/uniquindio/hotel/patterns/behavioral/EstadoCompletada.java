package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.exceptions.CancelacionInvalidaException;
import co.edu.uniquindio.hotel.exceptions.ReservaException;
import co.edu.uniquindio.hotel.model.Reserva;

public class EstadoCompletada implements EstadoReserva {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoEstadoReserva getTipo() {
        return TipoEstadoReserva.COMPLETADA;
    }

    @Override
    public void confirmar(Reserva reserva) throws ReservaException {
        throw new ReservaException("La reserva ya fue completada.");
    }

    @Override
    public void cancelar(Reserva reserva, boolean canceladoPorHotel) throws CancelacionInvalidaException {
        throw new CancelacionInvalidaException("No se puede cancelar una reserva cuya estadía ya ha concluido.");
    }

    @Override
    public void completar(Reserva reserva) throws ReservaException {
        // Ya completada
    }
}
