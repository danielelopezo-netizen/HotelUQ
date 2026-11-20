package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.exceptions.CancelacionInvalidaException;
import co.edu.uniquindio.hotel.exceptions.ReservaException;
import co.edu.uniquindio.hotel.model.Reserva;

import java.io.Serializable;

public interface EstadoReserva extends Serializable {
    TipoEstadoReserva getTipo();
    void confirmar(Reserva reserva) throws ReservaException;
    void cancelar(Reserva reserva, boolean canceladoPorHotel) throws CancelacionInvalidaException;
    void completar(Reserva reserva) throws ReservaException;
}

