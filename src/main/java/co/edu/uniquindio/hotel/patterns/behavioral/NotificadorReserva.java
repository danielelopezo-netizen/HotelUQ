package co.edu.uniquindio.hotel.patterns.behavioral;

import co.edu.uniquindio.hotel.model.Reserva;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificadorReserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<IReservaObserver> observadores = new ArrayList<>();

    public void suscribir(IReservaObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(IReservaObserver observador) {
        observadores.remove(observador);
    }

    public void notificarCreacion(Reserva reserva) {
        for (IReservaObserver obs : observadores) {
            obs.onReservaCreada(reserva);
        }
    }

    public void notificarCancelacion(Reserva reserva, double montoReembolso) {
        for (IReservaObserver obs : observadores) {
            obs.onReservaCancelada(reserva, montoReembolso);
        }
    }

    public void notificarCompletada(Reserva reserva) {
        for (IReservaObserver obs : observadores) {
            obs.onReservaCompletada(reserva);
        }
    }
}
