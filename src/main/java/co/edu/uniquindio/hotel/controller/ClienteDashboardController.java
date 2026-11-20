package co.edu.uniquindio.hotel.controller;

import co.edu.uniquindio.hotel.MainApp;
import co.edu.uniquindio.hotel.exceptions.HotelException;
import co.edu.uniquindio.hotel.model.*;
import co.edu.uniquindio.hotel.patterns.behavioral.TipoEstadoReserva;
import co.edu.uniquindio.hotel.patterns.creational.ReservaBuilder;
import co.edu.uniquindio.hotel.patterns.creational.SesionManager;
import co.edu.uniquindio.hotel.patterns.structural.ComponenteServicio;
import co.edu.uniquindio.hotel.patterns.structural.HotelFachada;
import co.edu.uniquindio.hotel.patterns.structural.SeguroCancelacionFlexibleDecorator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDashboardController {

    // Encabezado
    @FXML private Label lblNombreCliente;
    @FXML private Label lblSaldoHeader;
    @FXML private Label lblPuntosHeader;

    // Vistas / Pestañas
    @FXML private VBox panelReservar;
    @FXML private VBox panelTarjeta;
    @FXML private VBox panelPuntos;
    @FXML private VBox panelReservas;

    // Botones del menú lateral
    @FXML private Button btnMenuReservar;
    @FXML private Button btnMenuTarjeta;
    @FXML private Button btnMenuPuntos;
    @FXML private Button btnMenuReservas;

    // Pestaña 1: Explorar y Reservar
    @FXML private DatePicker dpEntrada;
    @FXML private DatePicker dpSalida;
    @FXML private ComboBox<String> cbTipoHabitacion;
    @FXML private Spinner<Integer> spHuespedes;
    @FXML private TableView<Habitacion> tblHabitacionesDisponibles;
    @FXML private TableColumn<Habitacion, String> colHabNumero;
    @FXML private TableColumn<Habitacion, String> colHabTipo;
    @FXML private TableColumn<Habitacion, String> colHabPiso;
    @FXML private TableColumn<Habitacion, String> colHabCapacidad;
    @FXML private TableColumn<Habitacion, String> colHabPrecio;
    @FXML private TableColumn<Habitacion, String> colHabDescripcion;

    // Configurador de Reserva (lado derecho de Pestaña 1)
    @FXML private VBox boxConfigurador;
    @FXML private Label lblHabitacionSeleccionada;
    @FXML private ListView<ComponenteServicio> lvServicios;
    @FXML private ComboBox<Promocion> cbPromociones;
    @FXML private CheckBox chkUsarPuntos;
    @FXML private CheckBox chkSeguroFlexible;
    @FXML private Label lblResumenNoches;
    @FXML private Label lblResumenHospedaje;
    @FXML private Label lblResumenLargaEstadia;
    @FXML private Label lblResumenPromocion;
    @FXML private Label lblResumenServicios;
    @FXML private Label lblResumenPuntosDcto;
    @FXML private Label lblResumenSeguro;
    @FXML private Label lblResumenTotalNeto;

    // Pestaña 2: Tarjeta Virtual
    @FXML private Label lblCardNumero;
    @FXML private Label lblCardTitular;
    @FXML private Label lblCardSaldoGrande;
    @FXML private TableView<MovimientoTarjeta> tblMovimientos;
    @FXML private TableColumn<MovimientoTarjeta, String> colMovFecha;
    @FXML private TableColumn<MovimientoTarjeta, String> colMovTipo;
    @FXML private TableColumn<MovimientoTarjeta, String> colMovConcepto;
    @FXML private TableColumn<MovimientoTarjeta, String> colMovMonto;
    @FXML private TableColumn<MovimientoTarjeta, String> colMovSaldo;

    // Pestaña 3: Puntos
    @FXML private Label lblPuntosVigentes;
    @FXML private Label lblPuntosVencidos;
    @FXML private Label lblPuntosEquivalenteCOP;
    @FXML private TableView<RegistroPuntos> tblLotesPuntos;
    @FXML private TableColumn<RegistroPuntos, String> colLotePuntos;
    @FXML private TableColumn<RegistroPuntos, String> colLoteObtencion;
    @FXML private TableColumn<RegistroPuntos, String> colLoteVencimiento;
    @FXML private TableColumn<RegistroPuntos, String> colLoteConcepto;
    @FXML private TableColumn<RegistroPuntos, String> colLoteEstado;
    @FXML private ListView<BeneficioPuntos> lvBeneficiosPuntos;

    // Pestaña 4: Mis Reservas
    @FXML private TableView<Reserva> tblMisReservas;
    @FXML private TableColumn<Reserva, String> colResCodigo;
    @FXML private TableColumn<Reserva, String> colResHabitacion;
    @FXML private TableColumn<Reserva, String> colResEntrada;
    @FXML private TableColumn<Reserva, String> colResSalida;
    @FXML private TableColumn<Reserva, String> colResHuespedes;
    @FXML private TableColumn<Reserva, String> colResTotal;
    @FXML private TableColumn<Reserva, String> colResEstado;
    @FXML private Label lblDetalleReservaServicios;

    private final HotelFachada fachada = HotelFachada.getInstance();
    private Cliente clienteActual;
    private Habitacion habitacionSeleccionada;

    @FXML
    public void initialize() {
        clienteActual = SesionManager.getInstance().getClienteActual();
        if (clienteActual == null) {
            MainApp.cargarVista("/co/edu/uniquindio/hotel/views/LoginView.fxml", "HotelUQ - Iniciar Sesión");
            return;
        }

        configurarEncabezado();
        configurarFiltrosYTablas();
        mostrarVistaReservar();
        actualizarDatosCliente();
    }

    private void configurarEncabezado() {
        lblNombreCliente.setText(clienteActual.getNombre());
        actualizarBadgesHeader();
    }

    private void actualizarBadgesHeader() {
        lblSaldoHeader.setText("$" + String.format("%,.0f", clienteActual.getTarjetaVirtual().getSaldo()));
        int puntosValidos = clienteActual.getCuentaPuntos().getPuntosValidos(LocalDate.now());
        lblPuntosHeader.setText(puntosValidos + " pts");
    }

    private void configurarFiltrosYTablas() {
        // Fechas por defecto: mañana a 3 días después
        dpEntrada.setValue(LocalDate.now().plusDays(1));
        dpSalida.setValue(LocalDate.now().plusDays(4));

        cbTipoHabitacion.getItems().addAll("Todas las Categorías", "Estándar", "Familiar", "Suite de Lujo", "Suite Presidencial");
        cbTipoHabitacion.getSelectionModel().selectFirst();

        spHuespedes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 2));

        // Configuración de columnas Tabla Habitaciones
        colHabNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero()));
        colHabTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().getNombreVisual()));
        colHabPiso.setCellValueFactory(c -> new SimpleStringProperty("Piso " + c.getValue().getPiso()));
        colHabCapacidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCapacidadMaxima() + " personas"));
        colHabPrecio.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getPrecioBaseNoche())));
        colHabDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        tblHabitacionesDisponibles.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                seleccionarHabitacion(newV);
            }
        });

        // Configuración de Servicios en el Configurador
        lvServicios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvServicios.setItems(FXCollections.observableArrayList(fachada.getService().getCatalogoServicios()));
        lvServicios.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> recalcularResumenReserva());

        // Promociones
        cbPromociones.setItems(FXCollections.observableArrayList(fachada.getService().getPromociones()));
        cbPromociones.setOnAction(e -> recalcularResumenReserva());

        chkUsarPuntos.setOnAction(e -> recalcularResumenReserva());
        chkSeguroFlexible.setOnAction(e -> recalcularResumenReserva());

        // Tabla Movimientos Tarjeta
        colMovFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHoraFormateada()));
        colMovTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        colMovConcepto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colMovMonto.setCellValueFactory(c -> new SimpleStringProperty((c.getValue().getMonto() >= 0 ? "+$" : "-$") + String.format("%,.0f", Math.abs(c.getValue().getMonto()))));
        colMovSaldo.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getSaldoPosterior())));

        // Tabla Puntos
        colLotePuntos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPuntosDisponibles() + "/" + c.getValue().getPuntosOriginales()));
        colLoteObtencion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaObtencionFormateada()));
        colLoteVencimiento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaVencimientoFormateada()));
        colLoteConcepto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConcepto()));
        colLoteEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().estaVencido(LocalDate.now()) ? "❌ Vencido" : "✅ Vigente"));

        lvBeneficiosPuntos.setItems(FXCollections.observableArrayList(fachada.getService().getCatalogoBeneficios()));

        // Tabla Mis Reservas
        colResCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colResHabitacion.setCellValueFactory(c -> new SimpleStringProperty("Hab " + c.getValue().getHabitacion().getNumero() + " (" + c.getValue().getHabitacion().getTipo().getNombreVisual() + ")"));
        colResEntrada.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaEntradaFormateada()));
        colResSalida.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaSalidaFormateada()));
        colResHuespedes.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroHuespedes() + " pers."));
        colResTotal.setCellValueFactory(c -> new SimpleStringProperty("$" + String.format("%,.0f", c.getValue().getTotalNeto())));
        colResEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoEstado().getNombreVisual()));

        tblMisReservas.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                StringBuilder sb = new StringBuilder("Servicios contratados: ");
                if (n.getServicios().isEmpty()) {
                    sb.append("Ninguno");
                } else {
                    n.getServicios().forEach(s -> sb.append(s.getNombre()).append(", "));
                }
                if (n.tieneSeguroCancelacionFlexible()) {
                    sb.append(" | [🛡️ Seguro de Cancelación 100% Flexible]");
                }
                lblDetalleReservaServicios.setText(sb.toString());
            }
        });
    }

    @FXML
    public void buscarHabitacionesDisponibles() {
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();
        int huespedes = spHuespedes.getValue();

        TipoHabitacion tipoFiltro = null;
        String seleccion = cbTipoHabitacion.getValue();
        if ("Estándar".equalsIgnoreCase(seleccion)) tipoFiltro = TipoHabitacion.ESTANDAR;
        else if ("Familiar".equalsIgnoreCase(seleccion)) tipoFiltro = TipoHabitacion.FAMILIAR;
        else if ("Suite de Lujo".equalsIgnoreCase(seleccion)) tipoFiltro = TipoHabitacion.SUITE;
        else if ("Suite Presidencial".equalsIgnoreCase(seleccion)) tipoFiltro = TipoHabitacion.PRESIDENCIAL;

        try {
            List<Habitacion> disponibles = fachada.consultarHabitacionesDisponibles(entrada, salida, tipoFiltro, huespedes);
            tblHabitacionesDisponibles.setItems(FXCollections.observableArrayList(disponibles));
            boxConfigurador.setVisible(false);
            if (disponibles.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Disponibilidad", "No hay habitaciones disponibles con los criterios seleccionados.");
            }
        } catch (HotelException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error de Consulta", e.getMessage());
        }
    }

    private void seleccionarHabitacion(Habitacion habitacion) {
        this.habitacionSeleccionada = habitacion;
        lblHabitacionSeleccionada.setText("Habitación " + habitacion.getNumero() + " (" + habitacion.getTipo() + ") - $" + String.format("%,.0f", habitacion.getPrecioBaseNoche()) + "/noche");
        boxConfigurador.setVisible(true);
        recalcularResumenReserva();
    }

    private void recalcularResumenReserva() {
        if (habitacionSeleccionada == null) return;

        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();
        int noches = Math.max(1, (int) java.time.temporal.ChronoUnit.DAYS.between(entrada, salida));
        lblResumenNoches.setText(noches + " noche(s)");

        double hospedajeBruto = habitacionSeleccionada.calcularTarifaBase(noches);
        lblResumenHospedaje.setText("$" + String.format("%,.0f", hospedajeBruto));

        // Regla Adicional 1: Descuento 10% por más de 7 noches
        double dctoLargaEstadia = (noches > 7) ? (hospedajeBruto * 0.10) : 0.0;
        lblResumenLargaEstadia.setText((dctoLargaEstadia > 0 ? "-$" : "$") + String.format("%,.0f", dctoLargaEstadia));

        // Promoción
        Promocion promo = cbPromociones.getValue();
        double dctoPromo = 0.0;
        if (promo != null && promo.esAplicable(habitacionSeleccionada, noches, entrada)) {
            dctoPromo = promo.calcularDescuento(hospedajeBruto);
        }
        lblResumenPromocion.setText((dctoPromo > 0 ? "-$" : "$") + String.format("%,.0f", dctoPromo));

        // Servicios seleccionados
        List<ComponenteServicio> servicios = lvServicios.getSelectionModel().getSelectedItems();
        double costoServicios = servicios.stream().mapToDouble(ComponenteServicio::getPrecio).sum();
        lblResumenServicios.setText("$" + String.format("%,.0f", costoServicios));

        // Puntos
        double dctoPuntos = 0.0;
        if (chkUsarPuntos.isSelected()) {
            int puntosValidos = clienteActual.getCuentaPuntos().getPuntosValidos(LocalDate.now());
            dctoPuntos = puntosValidos * CuentaPuntos.VALOR_POR_PUNTO_COP;
        }
        lblResumenPuntosDcto.setText((dctoPuntos > 0 ? "-$" : "$") + String.format("%,.0f", dctoPuntos));

        // Seguro Flexible (Decorator)
        double costoSeguro = chkSeguroFlexible.isSelected() ? SeguroCancelacionFlexibleDecorator.COSTO_SEGURO : 0.0;
        lblResumenSeguro.setText("$" + String.format("%,.0f", costoSeguro));

        double subtotal = Math.max(0.0, hospedajeBruto - dctoLargaEstadia - dctoPromo) + costoServicios + costoSeguro;
        double totalNeto = Math.max(0.0, subtotal - dctoPuntos);
        lblResumenTotalNeto.setText("$" + String.format("%,.0f", totalNeto));
    }

    @FXML
    public void confirmarYRealizarReserva() {
        if (habitacionSeleccionada == null) return;

        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();
        int huespedes = spHuespedes.getValue();

        try {
            ReservaBuilder builder = new ReservaBuilder()
                    .paraCliente(clienteActual)
                    .enHabitacion(habitacionSeleccionada)
                    .conFechas(entrada, salida)
                    .conHuespedes(huespedes)
                    .conServicios(new ArrayList<>(lvServicios.getSelectionModel().getSelectedItems()))
                    .aplicarPromocion(cbPromociones.getValue())
                    .conSeguroCancelacion(chkSeguroFlexible.isSelected());

            if (chkUsarPuntos.isSelected()) {
                int puntos = clienteActual.getCuentaPuntos().getPuntosValidos(LocalDate.now());
                double equiv = puntos * CuentaPuntos.VALOR_POR_PUNTO_COP;
                builder.redimirPuntos(puntos, equiv);
            }

            Reserva confirmada = fachada.confirmarReserva(builder);

            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Reserva Confirmada!",
                    "Su reserva (" + confirmada.getCodigo() + ") ha sido pagada exitosamente con su Tarjeta Virtual.\n"
                    + "Habitación: " + confirmada.getHabitacion().getNumero() + "\n"
                    + "Total Pagado: $" + String.format("%,.0f", confirmada.getTotalNeto()) + "\n"
                    + "¡Ha acumulado puntos por su compra!");

            actualizarDatosCliente();
            buscarHabitacionesDisponibles();
            mostrarVistaMisReservas();
        } catch (HotelException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo realizar la reserva", e.getMessage());
        }
    }

    @FXML
    public void cancelarReservaSeleccionada() {
        Reserva seleccionada = tblMisReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Seleccione una reserva de la lista para cancelar.");
            return;
        }

        if (seleccionada.getTipoEstado() == TipoEstadoReserva.CANCELADA) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", "Esta reserva ya se encuentra cancelada.");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLlegada = seleccionada.getFechaEntrada().atTime(15, 0);
        long horasRestantes = Duration.between(ahora, fechaLlegada).toHours();

        String textoReembolso;
        if (seleccionada.tieneSeguroCancelacionFlexible()) {
            textoReembolso = "100% (Cuenta con Seguro Flexible)";
        } else if (horasRestantes > 72) {
            textoReembolso = "100% (> 72 horas de anticipación)";
        } else if (horasRestantes >= 24) {
            textoReembolso = "80% (Entre 24 y 72 horas de anticipación)";
        } else {
            textoReembolso = "0% (Menos de 24 horas restantes. Regla 3.3)";
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Cancelación de Reserva");
        confirm.setHeaderText("Cancelación de Reserva " + seleccionada.getCodigo());
        confirm.setContentText("Horas restantes para la llegada: " + horasRestantes + " hrs.\n"
                + "Reembolso aplicable: " + textoReembolso + "\n"
                + "¿Está seguro de que desea cancelar esta reserva?");

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                double reembolso = fachada.cancelarReservaPorCliente(seleccionada.getCodigo());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cancelación Exitosa",
                        "La reserva ha sido cancelada.\nSe acreditaron $" + String.format("%,.0f", reembolso) 
                        + " a su Tarjeta Virtual HotelUQ.");
                actualizarDatosCliente();
            } catch (HotelException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Cancelar", e.getMessage());
            }
        }
    }

    @FXML
    public void solicitarRecargaTarjeta() {
        TextInputDialog dialog = new TextInputDialog("100000");
        dialog.setTitle("Solicitud de Recarga de Tarjeta Virtual");
        dialog.setHeaderText("Solicitar recarga al Administrador");
        dialog.setContentText("Ingrese el monto deseado ($ COP):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(montoStr -> {
            try {
                double monto = Double.parseDouble(montoStr);
                if (monto <= 0) throw new NumberFormatException();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Solicitud Registrada",
                        "Se ha notificado a la Administración del hotel la solicitud de recarga por $" 
                        + String.format("%,.0f", monto) + " COP.\nEl administrador efectuará la recarga desde su panel de control.");
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Monto Inválido", "Por favor ingrese un valor numérico válido mayor a 0.");
            }
        });
    }

    private void actualizarDatosCliente() {
        actualizarBadgesHeader();

        // Actualizar Tarjeta
        lblCardNumero.setText(clienteActual.getTarjetaVirtual().getNumeroTarjeta());
        lblCardTitular.setText(clienteActual.getNombre().toUpperCase());
        lblCardSaldoGrande.setText("$" + String.format("%,.0f", clienteActual.getTarjetaVirtual().getSaldo()) + " COP");
        tblMovimientos.setItems(FXCollections.observableArrayList(clienteActual.getTarjetaVirtual().getMovimientos()));

        // Actualizar Puntos
        int validos = clienteActual.getCuentaPuntos().getPuntosValidos(LocalDate.now());
        int vencidos = clienteActual.getCuentaPuntos().getPuntosVencidos(LocalDate.now());
        lblPuntosVigentes.setText(validos + " pts");
        lblPuntosVencidos.setText(vencidos + " pts");
        lblPuntosEquivalenteCOP.setText("$" + String.format("%,.0f", validos * CuentaPuntos.VALOR_POR_PUNTO_COP) + " COP");
        tblLotesPuntos.setItems(FXCollections.observableArrayList(clienteActual.getCuentaPuntos().getRegistros()));

        // Actualizar Reservas del cliente
        List<Reserva> misReservas = fachada.getService().getReservas().stream()
                .filter(r -> r.getCliente().equals(clienteActual))
                .toList();
        tblMisReservas.setItems(FXCollections.observableArrayList(misReservas));
    }

    // Navegación lateral entre secciones
    @FXML
    public void mostrarVistaReservar() {
        activarPanel(panelReservar, btnMenuReservar);
        buscarHabitacionesDisponibles();
    }

    @FXML
    public void mostrarVistaTarjeta() {
        activarPanel(panelTarjeta, btnMenuTarjeta);
        actualizarDatosCliente();
    }

    @FXML
    public void mostrarVistaPuntos() {
        activarPanel(panelPuntos, btnMenuPuntos);
        actualizarDatosCliente();
    }

    @FXML
    public void mostrarVistaMisReservas() {
        activarPanel(panelReservas, btnMenuReservas);
        actualizarDatosCliente();
    }

    private void activarPanel(VBox panel, Button boton) {
        panelReservar.setVisible(panel == panelReservar);
        panelReservar.setManaged(panel == panelReservar);
        panelTarjeta.setVisible(panel == panelTarjeta);
        panelTarjeta.setManaged(panel == panelTarjeta);
        panelPuntos.setVisible(panel == panelPuntos);
        panelPuntos.setManaged(panel == panelPuntos);
        panelReservas.setVisible(panel == panelReservas);
        panelReservas.setManaged(panel == panelReservas);

        btnMenuReservar.getStyleClass().removeAll("btn-menu-active");
        btnMenuTarjeta.getStyleClass().removeAll("btn-menu-active");
        btnMenuPuntos.getStyleClass().removeAll("btn-menu-active");
        btnMenuReservas.getStyleClass().removeAll("btn-menu-active");

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

