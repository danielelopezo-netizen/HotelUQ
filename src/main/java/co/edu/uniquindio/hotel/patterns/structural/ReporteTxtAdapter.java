package co.edu.uniquindio.hotel.patterns.structural;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ReporteTxtAdapter implements IExportadorReporte {

    @Override
    public void exportar(DatosReporte datos, File archivoDestino) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoDestino, StandardCharsets.UTF_8))) {
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                     HOTELUQ - SISTEMA DE GESTIÓN HOTELERA                     ");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("REPORTE: " + datos.getTitulo().toUpperCase());
            writer.newLine();
            writer.write("Fecha de Generación: " + datos.getFechaGeneracionFormateada());
            writer.newLine();
            writer.write("Periodo Consultado : " + datos.getPeriodoConsultado());
            writer.newLine();
            writer.write("Registros Hallados : " + datos.getCantidadRegistros());
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();

            // Encabezados
            if (datos.getEncabezadosColumnas() != null && !datos.getEncabezadosColumnas().isEmpty()) {
                writer.write(String.join(" | ", datos.getEncabezadosColumnas()));
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
            }

            // Filas
            for (List<String> fila : datos.getFilas()) {
                writer.write(String.join(" | ", fila));
                writer.newLine();
            }

            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            writer.write("RESUMEN DE TOTALES:");
            writer.newLine();
            for (Map.Entry<String, String> total : datos.getTotales().entrySet()) {
                writer.write(" * " + total.getKey() + ": " + total.getValue());
                writer.newLine();
            }
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Generado automáticamente por HotelUQ.");
            writer.newLine();
        }
    }

    @Override
    public String getExtension() {
        return ".txt";
    }

    @Override
    public String getNombreFormato() {
        return "Archivo de Texto (.txt)";
    }
}

