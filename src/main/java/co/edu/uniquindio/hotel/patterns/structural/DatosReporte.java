package co.edu.uniquindio.hotel.patterns.structural;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatosReporte implements Serializable {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private String periodoConsultado;
    private LocalDateTime fechaGeneracion;
    private List<String> encabezadosColumnas = new ArrayList<>();
    private List<List<String>> filas = new ArrayList<>();
    private Map<String, String> totales = new LinkedHashMap<>();

    public DatosReporte() {
        this.fechaGeneracion = LocalDateTime.now();
    }

    public DatosReporte(String titulo, String periodoConsultado) {
        this.titulo = titulo;
        this.periodoConsultado = periodoConsultado;
        this.fechaGeneracion = LocalDateTime.now();
    }

    public void agregarFila(String... valores) {
        filas.add(Arrays.asList(valores));
    }

    public void agregarTotal(String etiqueta, String valor) {
        totales.put(etiqueta, valor);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPeriodoConsultado() {
        return periodoConsultado;
    }

    public void setPeriodoConsultado(String periodoConsultado) {
        this.periodoConsultado = periodoConsultado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public String getFechaGeneracionFormateada() {
        return fechaGeneracion != null ? fechaGeneracion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
    }

    public List<String> getEncabezadosColumnas() {
        return encabezadosColumnas;
    }

    public void setEncabezadosColumnas(List<String> encabezadosColumnas) {
        this.encabezadosColumnas = encabezadosColumnas;
    }

    public List<List<String>> getFilas() {
        return filas;
    }

    public Map<String, String> getTotales() {
        return totales;
    }

    public int getCantidadRegistros() {
        return filas.size();
    }
}
