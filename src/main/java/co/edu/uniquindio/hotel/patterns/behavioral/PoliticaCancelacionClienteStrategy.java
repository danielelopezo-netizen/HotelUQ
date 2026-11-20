package co.edu.uniquindio.hotel.patterns.behavioral;

import java.time.Duration;
import java.time.LocalDateTime;

public class PoliticaCancelacionClienteStrategy implements PoliticaCancelacionStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public double calcularPorcentajeReembolso(LocalDateTime fechaLlegada, LocalDateTime fechaCancelacion) {
        if (fechaLlegada == null || fechaCancelacion == null) {
            return 0.0;
        }

        Duration duracion = Duration.between(fechaCancelacion, fechaLlegada);
        long horasRestantes = duracion.toHours();

        if (horasRestantes > 72) {
            return 100.0; // Más de 72 horas -> 100%
        } else if (horasRestantes >= 24) {
            return 80.0;  // Entre 24 y 72 horas -> 80%
        } else {
            return 0.0;   // Menos de 24 horas -> 0%
        }
    }

    @Override
    public String getNombrePolitica() {
        return "Política Estándar de Cancelación de Huésped";
    }
}

