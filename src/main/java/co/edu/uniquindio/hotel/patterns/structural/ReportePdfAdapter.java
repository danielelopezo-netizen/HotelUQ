package co.edu.uniquindio.hotel.patterns.structural;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adaptador de Reporte para formato PDF (.pdf) implementado en Java estándar puro.
 * Genera documentos PDF válidos (especificación PDF 1.4) con tablas, encabezados,
 * metadatos y secciones de totales, sin requerir dependencias binarias externas.
 */
public class ReportePdfAdapter implements IExportadorReporte {

    @Override
    public void exportar(DatosReporte datos, File archivoDestino) throws Exception {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoDestino))) {
            StringBuilder pdf = new StringBuilder();
            List<Integer> xrefOffsets = new ArrayList<>();

            // 1. Cabecera PDF
            pdf.append("%PDF-1.4\n");
            pdf.append("%\u00E2\u00E3\u00CF\u00D3\n");

            // Objeto 1: Catalog
            xrefOffsets.add(pdf.length());
            pdf.append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            // Objeto 2: Pages
            xrefOffsets.add(pdf.length());
            pdf.append("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

            // Objeto 4: Font Helvetica
            xrefOffsets.add(pdf.length());
            pdf.append("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

            // Objeto 5: Font Helvetica-Bold
            xrefOffsets.add(pdf.length());
            pdf.append("5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n");

            // Construir contenido visual del flujo (Stream)
            StringBuilder streamContent = new StringBuilder();

            // Fondo y barra superior institucional (Verde esmeralda HotelUQ: #104C34 -> 0.06 0.30 0.20)
            streamContent.append("0.06 0.30 0.20 rg\n");
            streamContent.append("30 760 535 50 re\n"); // Rectángulo encabezado
            streamContent.append("f\n");

            // Texto cabecera institucional
            streamContent.append("BT\n");
            streamContent.append("/F2 16 Tf\n"); // Helvetica-Bold 16pt
            streamContent.append("1.0 1.0 1.0 rg\n"); // Blanco
            streamContent.append("180 785 Td\n");
            streamContent.append("(HOTELUQ - GESTION HOTELERA) Tj\n");
            streamContent.append("ET\n");

            // Título del reporte
            streamContent.append("BT\n");
            streamContent.append("/F2 13 Tf\n");
            streamContent.append("0.1 0.1 0.1 rg\n");
            streamContent.append("40 735 Td\n");
            streamContent.append("(").append(sanitizar(datos.getTitulo())).append(") Tj\n");
            streamContent.append("ET\n");

            // Metadatos
            streamContent.append("BT\n");
            streamContent.append("/F1 9 Tf\n");
            streamContent.append("0.3 0.3 0.3 rg\n");
            streamContent.append("40 718 Td\n");
            streamContent.append("(Fecha de Emision: ").append(sanitizar(datos.getFechaGeneracionFormateada()))
                         .append("  |  Periodo: ").append(sanitizar(datos.getPeriodoConsultado()))
                         .append("  |  Registros: ").append(datos.getCantidadRegistros()).append(") Tj\n");
            streamContent.append("ET\n");

            // Línea divisoria
            streamContent.append("0.8 0.8 0.8 RG\n");
            streamContent.append("1 w\n");
            streamContent.append("40 705 m 565 705 l S\n");

            // Tabla de datos
            int y = 685;
            List<String> columnas = datos.getEncabezadosColumnas();
            if (columnas != null && !columnas.isEmpty()) {
                // Fondo encabezado tabla
                streamContent.append("0.06 0.30 0.20 rg\n");
                streamContent.append("40 ").append(y - 5).append(" 525 18 re f\n");

                streamContent.append("BT\n");
                streamContent.append("/F2 8 Tf\n");
                streamContent.append("1 1 1 rg\n");
                int x = 45;
                int anchoCol = 525 / Math.max(1, columnas.size());
                for (String col : columnas) {
                    streamContent.append(x).append(" ").append(y).append(" Td\n");
                    streamContent.append("(").append(sanitizar(col)).append(") Tj\n");
                    streamContent.append(-x).append(" ").append(-y).append(" Td\n");
                    x += anchoCol;
                }
                streamContent.append("ET\n");
                y -= 20;

                // Filas de datos
                boolean zebra = false;
                for (List<String> fila : datos.getFilas()) {
                    if (y < 80) break; // Límite de página

                    if (zebra) {
                        streamContent.append("0.96 0.97 0.96 rg\n");
                        streamContent.append("40 ").append(y - 4).append(" 525 14 re f\n");
                    }

                    streamContent.append("BT\n");
                    streamContent.append("/F1 8 Tf\n");
                    streamContent.append("0.1 0.1 0.1 rg\n");
                    x = 45;
                    for (String val : fila) {
                        streamContent.append(x).append(" ").append(y).append(" Td\n");
                        String textoCorto = val.length() > 22 ? val.substring(0, 19) + "..." : val;
                        streamContent.append("(").append(sanitizar(textoCorto)).append(") Tj\n");
                        streamContent.append(-x).append(" ").append(-y).append(" Td\n");
                        x += anchoCol;
                    }
                    streamContent.append("ET\n");
                    zebra = !zebra;
                    y -= 16;
                }
            }

            // Totales
            y -= 15;
            if (y >= 60 && !datos.getTotales().isEmpty()) {
                streamContent.append("0.90 0.94 0.91 rg\n");
                streamContent.append("40 ").append(y - (datos.getTotales().size() * 14)).append(" 320 ")
                             .append(datos.getTotales().size() * 14 + 18).append(" re f\n");

                streamContent.append("BT\n");
                streamContent.append("/F2 9 Tf\n");
                streamContent.append("0.06 0.30 0.20 rg\n");
                streamContent.append("48 ").append(y).append(" Td\n");
                streamContent.append("(RESUMEN DE TOTALES E INDICADORES:) Tj\n");
                streamContent.append("ET\n");

                y -= 14;
                for (Map.Entry<String, String> total : datos.getTotales().entrySet()) {
                    streamContent.append("BT\n");
                    streamContent.append("/F1 8 Tf\n");
                    streamContent.append("0.1 0.1 0.1 rg\n");
                    streamContent.append("50 ").append(y).append(" Td\n");
                    streamContent.append("(").append(sanitizar(total.getKey())).append(": ").append(sanitizar(total.getValue())).append(") Tj\n");
                    streamContent.append("ET\n");
                    y -= 13;
                }
            }

            // Pie de página institucional
            streamContent.append("BT\n");
            streamContent.append("/F1 7 Tf\n");
            streamContent.append("0.5 0.5 0.5 rg\n");
            streamContent.append("210 25 Td\n");
            streamContent.append("(HotelUQ - Universidad del Quindio - Sistema de Gestion Hotelera) Tj\n");
            streamContent.append("ET\n");

            byte[] streamBytes = streamContent.toString().getBytes(StandardCharsets.ISO_8859_1);

            // Objeto 6: Stream Content
            xrefOffsets.add(pdf.length());
            pdf.append("6 0 obj\n");
            pdf.append("<< /Length ").append(streamBytes.length).append(" >>\n");
            pdf.append("stream\n");
            pdf.append(streamContent);
            pdf.append("\nendstream\nendobj\n");

            // Objeto 3: Page (refiriendo fonts 4, 5 y stream 6)
            xrefOffsets.add(2, pdf.length()); // Posición para objeto 3
            String objPage = "3 0 obj\n" +
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842]\n" +
                    "/Contents 6 0 R\n" +
                    "/Resources << /Font << /F1 4 0 R /F2 5 0 R >> >>\n" +
                    ">>\nendobj\n";
            pdf.insert(xrefOffsets.get(2), objPage);

            // Ajustar offsets posteriores tras la inserción
            int delta = objPage.length();
            for (int i = 3; i < xrefOffsets.size(); i++) {
                xrefOffsets.set(i, xrefOffsets.get(i) + delta);
            }

            // Cross-Reference Table (xref)
            int startXref = pdf.length();
            pdf.append("xref\n");
            pdf.append("0 7\n");
            pdf.append("0000000000 65535 f \n");
            for (int offset : xrefOffsets) {
                pdf.append(String.format("%010d 00000 n \n", offset));
            }

            // Trailer
            pdf.append("trailer\n");
            pdf.append("<< /Size 7 /Root 1 0 R >>\n");
            pdf.append("startxref\n");
            pdf.append(startXref).append("\n");
            pdf.append("%%EOF\n");

            out.write(pdf.toString().getBytes(StandardCharsets.ISO_8859_1));
            out.flush();
        }
    }

    private String sanitizar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                    .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
                    .replace("ñ", "n").replace("Ñ", "N");
    }

    @Override
    public String getExtension() {
        return ".pdf";
    }

    @Override
    public String getNombreFormato() {
        return "Documento PDF (.pdf)";
    }
}
