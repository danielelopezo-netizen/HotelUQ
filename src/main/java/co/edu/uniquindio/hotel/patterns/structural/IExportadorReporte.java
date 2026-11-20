package co.edu.uniquindio.hotel.patterns.structural;

import java.io.File;

public interface IExportadorReporte {
    void exportar(DatosReporte datos, File archivoDestino) throws Exception;
    String getExtension();
    String getNombreFormato();
}
