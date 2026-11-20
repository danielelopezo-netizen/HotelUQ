package co.edu.uniquindio.hotel.model;

public enum TipoHabitacion {
    ESTANDAR("Estándar", 2, "Cama Queen, WiFi alta velocidad, TV Smart, baño privado."),
    FAMILIAR("Familiar", 5, "Dos camas Queen y un sofá cama, área de estar, frigobar, WiFi, balcón."),
    SUITE("Suite de Lujo", 4, "Cama King, sala de estar independiente, jacuzzi, vista panorámica, minibar."),
    PRESIDENCIAL("Suite Presidencial", 6, "Dos habitaciones King, jacuzzi, terraza privada, comedor, servicio de mayordomo.");

    private final String nombreVisual;
    private final int capacidadMaxima;
    private final String descripcion;

    TipoHabitacion(String nombreVisual, int capacidadMaxima, String descripcion) {
        this.nombreVisual = nombreVisual;
        this.capacidadMaxima = capacidadMaxima;
        this.descripcion = descripcion;
    }

    public String getNombreVisual() {
        return nombreVisual;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombreVisual;
    }
}
