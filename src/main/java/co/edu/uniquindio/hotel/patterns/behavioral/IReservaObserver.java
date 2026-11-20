package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.model.Reserva;

import java.io.Serializable;

public interface IReservaObserver extends Serializable {
    void onReservaCreada(Reserva reserva);
    void onReservaCancelada(Reserva reserva, double montoReembolso);
    void onReservaCompletada(Reserva reserva);
}

