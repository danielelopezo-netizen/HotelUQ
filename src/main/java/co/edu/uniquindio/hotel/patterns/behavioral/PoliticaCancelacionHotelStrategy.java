package co.edu.uniquindio.hotel.patterns.behavioral;

import java.time.LocalDateTime;

public class PoliticaCancelacionHotelStrategy implements PoliticaCancelacionStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularPorcentajeReembolso(LocalDateTime fechaLlegada, LocalDateTime fechaCancelacion) {
        // Cuando el hotel cancela la reserva, la regla de negocio 3.3 exige devolver el 100%
        return 100.0;
    }

    @Override
    public String getNombrePolitica() {
        return "Cancelación Administrativa por parte del Hotel (Reembolso 100% garantizado)";
    }
}

