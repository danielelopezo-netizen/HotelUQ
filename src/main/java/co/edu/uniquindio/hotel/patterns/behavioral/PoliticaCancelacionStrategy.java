package co.edu.uniquindio.hotel.patterns.behavioral;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public interface PoliticaCancelacionStrategy extends Serializable {
    double calcularPorcentajeReembolso(LocalDateTime fechaLlegada, LocalDateTime fechaCancelacion);
    String getNombrePolitica();
}
