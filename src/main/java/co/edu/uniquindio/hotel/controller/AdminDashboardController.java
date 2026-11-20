package co.edu.uniquindio.hotel.controller;

import co.edu.uniquindio.hotel.MainApp;
import co.edu.uniquindio.hotel.exceptions.HotelException;
import co.edu.uniquindio.hotel.model.*;
import co.edu.uniquindio.hotel.patterns.behavioral.*;
import co.edu.uniquindio.hotel.patterns.creational.HabitacionFactory;
import co.edu.uniquindio.hotel.patterns.creational.SesionManager;
import co.edu.uniquindio.hotel.patterns.structural.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminDashboardController {

    // Encabezado
    @FXML private Label lblNombreAdmin;

    // Vistas / Pestañas de Navegación
    @FXML private VBox panelEstadisticas;
    @FXML private VBox panelHabitaciones;
    @FXML private VBox panelServicios;
    @FXML private VBox panelPromociones;
    @FXML private VBox panelRecargas;
    @FXML private VBox panelReservasHotel;
    @FXML private VBox panelReportes;

    // Botones del menú lateral
    @FXML private Button btnMenuEstadisticas;
    @FXML private Button btnMenuHabitaciones;
    @FXML private Button btnMenuServicios;
    @FXML private Button btnMenuPromociones;
    @FXML private Button btnMenuRecargas;
    @FXML private Button btnMenuReservasHotel;
    @FXML private Button btnMenuReportes;

    // Pestaña 1: Estadísticas y Gráficos JavaFX
    @FXML private Label lblKpiTotalReservas;
    @FXML private Label lblKpiIngresosTotales;
    @FXML private Label lblKpiReembolsosTotales;
    @FXML private Label lblKpiTasaOcupacion;
    @FXML private BarChart<String, Number> chartIngresosPorTipo;
    @FXML private PieChart chartDistribucionHabitaciones;
    @FXML private LineChart<String, Number> chartTendenciaIngresos;

    // Pestaña 2: Habitaciones
    @FXML private TableView<Habitacion> tblHabitacionesAdmin;
    @FXML private TableColumn<Habitacion, String> colHabNumAdmin;
    @FXML private TableColumn<Habitacion, String> colHabTipoAdmin;
    @FXML private TableColumn<Habitacion, String> colHabPisoAdmin;
    @FXML private TableColumn<Habitacion, String> colHabPrecioAdmin;
    @FXML private TableColumn<Habitacion, String> colHabCapAdmin;
    @FXML private TableColumn<Habitacion, String> colHabEstadoAdmin;
    @FXML private TextField txtHabNumero;
    @FXML private TextField txtHabPiso;
    @FXML private ComboBox<TipoHabitacion> cbHabTipo;
    @FXML private TextField txtHabPrecio;
    @FXML private TextField txtHabDescripcion;

    // Pestaña 3: Servicios
    @FXML private TableView<ComponenteServicio> tblServiciosAdmin;
    @FXML private TableColumn<ComponenteServicio, String> colSrvIdAdmin;
    @FXML private TableColumn<ComponenteServicio, String> colSrvNombreAdmin;
    @FXML private TableColumn<ComponenteServicio, String> colSrvTipoAdmin;
    @FXML private TableColumn<ComponenteServicio, String> colSrvPrecioAdmin;
    @FXML private TableColumn<ComponenteServicio, String> colSrvDescAdmin;
    @FXML private TextField txtSrvNombre;
    @FXML private TextField txtSrvPrecio;
    @FXML private TextField txtSrvDesc;

    // Pestaña 4: Promociones
    @FXML private TableView<Promocion> tblPromocionesAdmin;
    @FXML private TableColumn<Promocion, String> colPromoCodigoAdmin;
    @FXML private TableColumn<Promocion, String> colPromoNombreAdmin;
    @FXML private TableColumn<Promocion, String> colPromoDctoAdmin;
    @FXML private TableColumn<Promocion, String> colPromoNochesAdmin;
    @FXML private TableColumn<Promocion, String> colPromoEstadoAdmin;
    @FXML private TextField txtPromoCodigo;
    @FXML private TextField txtPromoNombre;
    @FXML private TextField txtPromoDcto;
    @FXML private TextField txtPromoNoches;

    // Pestaña 5: Caja de Recargas
    @FXML private ComboBox<Cliente> cbClientesRecarga;
    @FXML private Label lblSaldoActualClienteRecarga;
    @FXML private TextField txtMontoRecarga;
    @FXML private TextField txtDetalleRecarga;

    // Pestaña 6: Reservas del Hotel
    @FXML private TableView<Reserva> tblReservasHotel;
    @FXML private TableColumn<Reserva, String> colResCodHotel;
    @FXML private TableColumn<Reserva, String> colResClienteHotel;
    @FXML private TableColumn<Reserva, String> colResHabHotel;
    @FXML private TableColumn<Reserva, String> colResFechasHotel;
    @FXML private TableColumn<Reserva, String> colResTotalHotel;
    @FXML private TableColumn<Reserva, String> colResEstadoHotel;

    // Pestaña 7: Reportes Exportables
    @FXML private ComboBox<String> cbTipoReporte;
    @FXML private ComboBox<String> cbFormatoReporte;
    @FXML private DatePicker dpReporteInicio;
    @FXML private DatePicker dpReporteFin;
    @FXML private TextArea txtPreviewReporte;

    private final HotelFachada fachada = HotelFachada.getInstance();

    @FXML
    public void initialize() {
        Administrador adminActual = SesionManager.getInstance().getAdministradorActual();
        if (adminActual != null) {
            lblNombreAdmin.setText(adminActual.getNombre());
        }

        configurarTablas();
        configurarFormularios();
        mostrarVistaEstadisticas();
    }

    private void configurarTablas() {
        // Habitaciones
        colHabNumAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero()));
        colHabTipoAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().getNombreVisual()));
        colHabPisoAdmin.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPiso())));
        colHabPrecioAdmin.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getPrecioBaseNoche())));
        colHabCapAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCapacidadMaxima() + " pers."));
        colHabEstadoAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "🟢 Activa" : "🔴 Inactiva"));

        // Servicios
        colSrvIdAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colSrvNombreAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colSrvTipoAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isPaquete() ? "📦 Paquete Composite" : "⭐ Individual"));
        colSrvPrecioAdmin.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getPrecio())));
        colSrvDescAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        // Promociones
        colPromoCodigoAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colPromoNombreAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPromoDctoAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPorcentajeDescuento() + "%"));
        colPromoNochesAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNochesMinimas() + " noches"));
        colPromoEstadoAdmin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "🟢 Vigente" : "🔴 Inactiva"));

        // Reservas Hotel
        colResCodHotel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colResClienteHotel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre()));
        colResHabHotel.setCellValueFactory(c -> new SimpleStringProperty("Hab " + c.getValue().getHabitacion().getNumero() + " (" + c.getValue().getHabitacion().getTipo().getNombreVisual() + ")"));
        colResFechasHotel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaEntradaFormateada() + " al " + c.getValue().getFechaSalidaFormateada()));
        colResTotalHotel.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getTotalNeto())));
        colResEstadoHotel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoEstado().getNombreVisual()));
    }

    private void configurarFormularios() {
        cbHabTipo.setItems(FXCollections.observableArrayList(TipoHabitacion.values()));
        cbHabTipo.getSelectionModel().selectFirst();

        cbClientesRecarga.setItems(FXCollections.observableArrayList(fachada.getService().getClientes()));
        cbClientesRecarga.setOnAction(e -> {
            Cliente sel = cbClientesRecarga.getValue();
            if (sel != null) {
                lblSaldoActualClienteRecarga.setText("$" + String.format("%,.0f", sel.getTarjetaVirtual().getSaldo()) + " COP");
            }
        });

        cbTipoReporte.getItems().addAll("Reporte Financiero de Ingresos y Reembolsos", "Reporte Operativo de Ocupación");
        cbTipoReporte.getSelectionModel().selectFirst();

        cbFormatoReporte.getItems().addAll("Documento PDF (.pdf)", "Archivo de Texto (.txt)");
        cbFormatoReporte.getSelectionModel().selectFirst();

        dpReporteInicio.setValue(LocalDate.now().minusMonths(1));
        dpReporteFin.setValue(LocalDate.now().plusMonths(1));
    }

    // -------------------------------------------------------------
    // GESTIÓN DE REPORTES Y ESTADÍSTICAS VISUALES (CHARTS)
    // -------------------------------------------------------------
    private void actualizarEstadisticas() {
        List<Reserva> reservas = fachada.getService().getReservas();

        long totalReservas = reservas.size();
        double totalIngresos = reservas.stream()
                .filter(r -> r.getTipoEstado() != TipoEstadoReserva.CANCELADA)
                .mapToDouble(Reserva::getTotalNeto)
                .sum();
        double totalReembolsos = reservas.stream()
                .filter(Reserva::isFueReembolsado)
                .mapToDouble(Reserva::getMontoReembolsado)
                .sum();

        long totalHabitaciones = fachada.getService().getHabitaciones().size();
        long habitacionesOcupadas = reservas.stream()
                .filter(r -> r.getTipoEstado() == TipoEstadoReserva.CONFIRMADA)
                .map(r -> r.getHabitacion().getNumero())
                .distinct()
                .count();

        double tasaOcupacion = totalHabitaciones > 0 ? ((double) habitacionesOcupadas / totalHabitaciones) * 100.0 : 0.0;

        lblKpiTotalReservas.setText(String.valueOf(totalReservas));
        lblKpiIngresosTotales.setText("$" + String.format("%,.0f", totalIngresos));
        lblKpiReembolsosTotales.setText("$" + String.format("%,.0f", totalReembolsos));
        lblKpiTasaOcupacion.setText(String.format("%.1f", tasaOcupacion) + "%");

        // 1. BarChart: Ingresos por Tipo de Habitación
        chartIngresosPorTipo.getData().clear();
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
        seriesBar.setName("Ingresos ($ COP)");

        Map<TipoHabitacion, Double> ingresosPorTipo = new HashMap<>();
        for (Reserva r : reservas) {
            if (r.getTipoEstado() != TipoEstadoReserva.CANCELADA) {
                TipoHabitacion tipo = r.getHabitacion().getTipo();
                ingresosPorTipo.put(tipo, ingresosPorTipo.getOrDefault(tipo, 0.0) + r.getTotalNeto());
            }
        }
        for (Map.Entry<TipoHabitacion, Double> entry : ingresosPorTipo.entrySet()) {
            seriesBar.getData().add(new XYChart.Data<>(entry.getKey().getNombreVisual(), entry.getValue()));
        }
        chartIngresosPorTipo.getData().add(seriesBar);

        // 2. PieChart: Distribución de Reservas por Tipo
        chartDistribucionHabitaciones.getData().clear();
        Map<TipoHabitacion, Long> conteoPorTipo = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getHabitacion().getTipo(), Collectors.counting()));
        for (Map.Entry<TipoHabitacion, Long> entry : conteoPorTipo.entrySet()) {
            chartDistribucionHabitaciones.getData().add(new PieChart.Data(entry.getKey().getNombreVisual() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        // 3. LineChart: Comportamiento de Ingresos en el tiempo
        chartTendenciaIngresos.getData().clear();
        XYChart.Series<String, Number> seriesLine = new XYChart.Series<>();
        seriesLine.setName("Ingresos Generados");

        Map<LocalDate, Double> ingresosPorFecha = new TreeMap<>();
        for (Reserva r : reservas) {
            if (r.getTipoEstado() != TipoEstadoReserva.CANCELADA) {
                LocalDate f = r.getFechaEntrada();
                ingresosPorFecha.put(f, ingresosPorFecha.getOrDefault(f, 0.0) + r.getTotalNeto());
            }
        }
        for (Map.Entry<LocalDate, Double> entry : ingresosPorFecha.entrySet()) {
            seriesLine.getData().add(new XYChart.Data<>(entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM")), entry.getValue()));
        }
        chartTendenciaIngresos.getData().add(seriesLine);
    }

    // -------------------------------------------------------------
    // ACCIONES DE GESTIÓN
    // -------------------------------------------------------------
    @FXML
    public void agregarHabitacion() {
        try {
            String numero = txtHabNumero.getText().trim();
            int piso = Integer.parseInt(txtHabPiso.getText().trim());
            TipoHabitacion tipo = cbHabTipo.getValue();
            double precio = Double.parseDouble(txtHabPrecio.getText().trim());
            String desc = txtHabDescripcion.getText().trim();

            if (numero.isEmpty()) throw new HotelException("El número de habitación es obligatorio.");

            // Usando Factory Method
            Habitacion nueva = HabitacionFactory.crearHabitacion(tipo, numero, piso, precio, desc);
            fachada.getService().getHabitaciones().add(nueva);
            tblHabitacionesAdmin.setItems(FXCollections.observableArrayList(fachada.getService().getHabitaciones()));

            txtHabNumero.clear();
            txtHabPiso.clear();
            txtHabPrecio.clear();
            txtHabDescripcion.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Habitación creada exitosamente con Factory Method.");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Datos Inválidos", "Piso y Tarifa deben ser valores numéricos válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void agregarServicio() {
        try {
            String nombre = txtSrvNombre.getText().trim();
            double precio = Double.parseDouble(txtSrvPrecio.getText().trim());
            String desc = txtSrvDesc.getText().trim();

            if (nombre.isEmpty()) throw new HotelException("El nombre del servicio es obligatorio.");

            String id = "SRV-" + (fachada.getService().getCatalogoServicios().size() + 1);
            ServicioSimple s = new ServicioSimple(id, nombre, desc, precio);
            fachada.getService().getCatalogoServicios().add(s);
            tblServiciosAdmin.setItems(FXCollections.observableArrayList(fachada.getService().getCatalogoServicios()));

            txtSrvNombre.clear();
            txtSrvPrecio.clear();
            txtSrvDesc.clear();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Servicio adicional registrado.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void agregarPromocion() {
        try {
            String codigo = txtPromoCodigo.getText().trim().toUpperCase();
            String nombre = txtPromoNombre.getText().trim();
            double dcto = Double.parseDouble(txtPromoDcto.getText().trim());
            int noches = Integer.parseInt(txtPromoNoches.getText().trim());

            if (codigo.isEmpty() || nombre.isEmpty()) throw new HotelException("Código y nombre son obligatorios.");

            String id = "PRM-" + (fachada.getService().getPromociones().size() + 1);
            Promocion p = new Promocion(id, codigo, nombre, dcto, noches, null, LocalDate.now(), LocalDate.now().plusMonths(6));
            fachada.getService().getPromociones().add(p);
            tblPromocionesAdmin.setItems(FXCollections.observableArrayList(fachada.getService().getPromociones()));

            txtPromoCodigo.clear();
            txtPromoNombre.clear();
            txtPromoDcto.clear();
            txtPromoNoches.clear();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Promoción creada y activada.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void procesarRecargaTarjeta() {
        Cliente cliente = cbClientesRecarga.getValue();
        if (cliente == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un cliente para realizar la recarga.");
            return;
        }

        try {
            double monto = Double.parseDouble(txtMontoRecarga.getText().trim());
            String detalle = txtDetalleRecarga.getText().trim();
            if (detalle.isEmpty()) detalle = "Recarga autorizada por el Administrador";

            fachada.recargarTarjetaVirtual(cliente.getIdentificacion(), monto, detalle);
            lblSaldoActualClienteRecarga.setText("$" + String.format("%,.0f", cliente.getTarjetaVirtual().getSaldo()) + " COP");
            txtMontoRecarga.clear();
            txtDetalleRecarga.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Recarga Exitosa",
                    "Se han acreditado exitosamente $" + String.format("%,.0f", monto) 
                    + " a la tarjeta (" + cliente.getTarjetaVirtual().getNumeroTarjeta() + ") de " + cliente.getNombre());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Monto Inválido", "Ingrese un monto numérico válido mayor a cero.");
        } catch (HotelException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en Recarga", e.getMessage());
        }
    }

    @FXML
    public void cancelarReservaPorHotel() {
        Reserva seleccionada = tblReservasHotel.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione una reserva de la lista para cancelar.");
            return;
        }

        if (seleccionada.getTipoEstado() == TipoEstadoReserva.CANCELADA) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", "La reserva ya está cancelada.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("Mantenimiento no programado / Caso fortuito");
        dialog.setTitle("Cancelación Administrativa por parte del Hotel");
        dialog.setHeaderText("Regla de Negocio 3.3: Devolución incondicional del 100% ($" + String.format("%,.0f", seleccionada.getTotalNeto()) + " COP)");
        dialog.setContentText("Motivo de la cancelación por parte del hotel:");

        Optional<String> motivoOpt = dialog.showAndWait();
        if (motivoOpt.isPresent() && !motivoOpt.get().trim().isEmpty()) {
            try {
                double reembolso = fachada.cancelarReservaPorHotel(seleccionada.getCodigo(), motivoOpt.get().trim());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cancelación Administrativa Exitosa",
                        "Reserva cancelada. Se devolvió el 100% ($" + String.format("%,.0f", reembolso) 
                        + " COP) a la tarjeta virtual del cliente " + seleccionada.getCliente().getNombre());
                tblReservasHotel.refresh();
                actualizarEstadisticas();
            } catch (HotelException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------
    // EXPORTACIÓN DE REPORTES (TXT Y PDF)
    // -------------------------------------------------------------
    @FXML
    public void previsualizarReporte() {
        PlantillaReporte plantilla = obtenerPlantillaSeleccionada();
        LocalDate inicio = dpReporteInicio.getValue();
        LocalDate fin = dpReporteFin.getValue();

        DatosReporte datos = plantilla.generar(fachada.getService().getReservas(), inicio, fin);

        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append("                 HOTELUQ - REPORTE ADMINISTRATIVO                    \n");
        sb.append("======================================================================\n");
        sb.append("TÍTULO  : ").append(datos.getTitulo()).append("\n");
        sb.append("PERIODO : ").append(datos.getPeriodoConsultado()).append("\n");
        sb.append("EMISIÓN : ").append(datos.getFechaGeneracionFormateada()).append("\n");
        sb.append("TOTALES :\n");
        for (Map.Entry<String, String> entry : datos.getTotales().entrySet()) {
            sb.append("  * ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.join(" | ", datos.getEncabezadosColumnas())).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        for (List<String> f : datos.getFilas()) {
            sb.append(String.join(" | ", f)).append("\n");
        }
        sb.append("======================================================================\n");

        txtPreviewReporte.setText(sb.toString());
    }

    @FXML
    public void exportarReporteArchivo() {
        PlantillaReporte plantilla = obtenerPlantillaSeleccionada();
        IExportadorReporte exportador = obtenerExportadorSeleccionado();
        LocalDate inicio = dpReporteInicio.getValue();
        LocalDate fin = dpReporteFin.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Oficial HotelUQ");
        fileChooser.setInitialFileName("Reporte_HotelUQ_" + System.currentTimeMillis() + exportador.getExtension());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(exportador.getNombreFormato(), "*" + exportador.getExtension()));

        File archivo = fileChooser.showSaveDialog(null);
        if (archivo != null) {
            try {
                fachada.exportarReporte(plantilla, exportador, inicio, fin, archivo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte Generado",
                        "El reporte ha sido exportado exitosamente en:\n" + archivo.getAbsolutePath());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Exportar", e.getMessage());
            }
        }
    }

    private PlantillaReporte obtenerPlantillaSeleccionada() {
        String sel = cbTipoReporte.getValue();
        if (sel != null && sel.contains("Ocupación")) {
            return new ReporteOcupacionTemplate();
        }
        return new ReporteIngresosTemplate();
    }

    private IExportadorReporte obtenerExportadorSeleccionado() {
        String sel = cbFormatoReporte.getValue();
        if (sel != null && sel.contains("PDF")) {
            return new ReportePdfAdapter();
        }
        return new ReporteTxtAdapter();
    }

    // -------------------------------------------------------------
    // NAVEGACIÓN ENTRE PANELES
    // -------------------------------------------------------------
    @FXML public void mostrarVistaEstadisticas() { activarPanel(panelEstadisticas, btnMenuEstadisticas); actualizarEstadisticas(); }
    @FXML public void mostrarVistaHabitaciones() { activarPanel(panelHabitaciones, btnMenuHabitaciones); tblHabitacionesAdmin.setItems(FXCollections.observableArrayList(fachada.getService().getHabitaciones())); }
    @FXML public void mostrarVistaServicios() { activarPanel(panelServicios, btnMenuServicios); tblServiciosAdmin.setItems(FXCollections.observableArrayList(fachada.getService().getCatalogoServicios())); }
    @FXML public void mostrarVistaPromociones() { activarPanel(panelPromociones, btnMenuPromociones); tblPromocionesAdmin.setItems(FXCollections.observableArrayList(fachada.getService().getPromociones())); }
    @FXML public void mostrarVistaRecargas() { activarPanel(panelRecargas, btnMenuRecargas); cbClientesRecarga.setItems(FXCollections.observableArrayList(fachada.getService().getClientes())); }
    @FXML public void mostrarVistaReservasHotel() { activarPanel(panelReservasHotel, btnMenuReservasHotel); tblReservasHotel.setItems(FXCollections.observableArrayList(fachada.getService().getReservas())); }
    @FXML public void mostrarVistaReportes() { activarPanel(panelReportes, btnMenuReportes); previsualizarReporte(); }

    private void activarPanel(VBox panel, Button boton) {
        panelEstadisticas.setVisible(panel == panelEstadisticas); panelEstadisticas.setManaged(panel == panelEstadisticas);
        panelHabitaciones.setVisible(panel == panelHabitaciones); panelHabitaciones.setManaged(panel == panelHabitaciones);
        panelServicios.setVisible(panel == panelServicios); panelServicios.setManaged(panel == panelServicios);
        panelPromociones.setVisible(panel == panelPromociones); panelPromociones.setManaged(panel == panelPromociones);
        panelRecargas.setVisible(panel == panelRecargas); panelRecargas.setManaged(panel == panelRecargas);
        panelReservasHotel.setVisible(panel == panelReservasHotel); panelReservasHotel.setManaged(panel == panelReservasHotel);
        panelReportes.setVisible(panel == panelReportes); panelReportes.setManaged(panel == panelReportes);

        btnMenuEstadisticas.getStyleClass().removeAll("btn-menu-active");
        btnMenuHabitaciones.getStyleClass().removeAll("btn-menu-active");
        btnMenuServicios.getStyleClass().removeAll("btn-menu-active");
        btnMenuPromociones.getStyleClass().removeAll("btn-menu-active");
        btnMenuRecargas.getStyleClass().removeAll("btn-menu-active");
        btnMenuReservasHotel.getStyleClass().removeAll("btn-menu-active");
        btnMenuReportes.getStyleClass().removeAll("btn-menu-active");

        if (!boton.getStyleClass().contains("btn-menu-active")) {
            boton.getStyleClass().add("btn-menu-active");
        }
    }

    @FXML
    public void cerrarSesion() {
        SesionManager.getInstance().cerrarSesion();
        MainApp.cargarVista("/co/edu/uniquindio/hotel/views/LoginView.fxml", "HotelUQ - Iniciar Sesión");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

