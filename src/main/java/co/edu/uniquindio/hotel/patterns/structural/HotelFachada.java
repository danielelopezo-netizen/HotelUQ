package co.edu.uniquindio.hotel.patterns.structural;

import co.edu.uniquindio.hotel.exceptions.*;
import co.edu.uniquindio.hotel.model.*;
import co.edu.uniquindio.hotel.patterns.behavioral.*;
import co.edu.uniquindio.hotel.patterns.creational.ReservaBuilder;
import co.edu.uniquindio.hotel.patterns.creational.SesionManager;
import co.edu.uniquindio.hotel.service.HotelUQService;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HotelFachada {
    private static HotelFachada instance;

    private final HotelUQService service;
    private final PoliticaCancelacionStrategy politicaCancelacionCliente = new PoliticaCancelacionClienteStrategy();
    private final PoliticaCancelacionStrategy politicaCancelacionHotel = new PoliticaCancelacionHotelStrategy();

    private HotelFachada() {
        this.service = HotelUQService.getInstance();
    }

    public static synchronized HotelFachada getInstance() {
        if (instance == null) {
            instance = new HotelFachada();
        }
        return instance;
    }

    public Persona autenticar(String email, String password) throws AutenticacionException {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            throw new AutenticacionException("Debe ingresar correo y contraseña.");
        }

        // Buscar administrador
        Optional<Administrador> admin = service.buscarAdministradorPorEmail(email.trim());
        if (admin.isPresent() && admin.get().getPassword().equals(password)) {
            SesionManager.getInstance().iniciarSesion(admin.get());
            return admin.get();
        }

        // Buscar cliente
        Optional<Cliente> cliente = service.buscarClientePorEmail(email.trim());
        if (cliente.isPresent() && cliente.get().getPassword().equals(password)) {
            SesionManager.getInstance().iniciarSesion(cliente.get());
            return cliente.get();
        }

        throw new AutenticacionException("Credenciales incorrectas. Verifique su correo y contraseña.");
    }

    public Cliente registrarCliente(String identificacion, String nombre, String email, String telefono, String password) throws HotelException {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new HotelException("La identificación es obligatoria.");
        }
        if (service.buscarClientePorIdentificacion(identificacion).isPresent()) {
            throw new HotelException("Ya existe un cliente registrado con la identificación: " + identificacion);
        }
        if (service.buscarClientePorEmail(email).isPresent()) {
            throw new HotelException("Ya existe un usuario registrado con el correo: " + email);
        }

        Cliente nuevo = new Cliente(identificacion.trim(), nombre.trim(), email.trim(), telefono.trim(), password);
        service.getClientes().add(nuevo);
        return nuevo;
    }

    public List<Habitacion> consultarHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida,
                                                             TipoHabitacion tipoFiltro, int huespedes) throws ReservaException {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new ReservaException("Debe seleccionar las fechas de entrada y salida.");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new ReservaException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }

        return service.getHabitaciones().stream()
                .filter(Habitacion::isActiva)
                .filter(h -> tipoFiltro == null || h.getTipo() == tipoFiltro)
                .filter(h -> huespedes <= 0 || h.getCapacidadMaxima() >= huespedes)
                .filter(h -> habitacionEstaDisponible(h, fechaEntrada, fechaSalida))
                .collect(Collectors.toList());
    }

    public boolean habitacionEstaDisponible(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        return service.getReservas().stream()
                .filter(r -> r.getHabitacion().equals(habitacion))
                .noneMatch(r -> r.seSuperponeCon(entrada, salida));
    }

    public synchronized Reserva confirmarReserva(ReservaBuilder builder) throws HotelException {
        Reserva reserva = builder.build();

        // 1. Doble verificación de disponibilidad en tiempo real
        if (!habitacionEstaDisponible(reserva.getHabitacion(), reserva.getFechaEntrada(), reserva.getFechaSalida())) {
            throw new HabitacionNoDisponibleException("La habitación " + reserva.getHabitacion().getNumero() 
                    + " ya no se encuentra disponible para las fechas seleccionadas.");
        }

        Cliente cliente = reserva.getCliente();
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();

        // 2. Aplicar redención de puntos si se configuró en el builder
        if (reserva.getPuntosRedimidos() > 0) {
            cliente.getCuentaPuntos().redimirPuntos(reserva.getPuntosRedimidos(), LocalDate.now());
        }

        // 3. Debitar monto total neto de la tarjeta virtual
        double montoAPagar = reserva.getTotalNeto();
        if (reserva.tieneSeguroCancelacionFlexible()) {
            montoAPagar += SeguroCancelacionFlexibleDecorator.COSTO_SEGURO;
        }

        String concepto = "Pago Reserva " + reserva.getCodigo() + " - Habitación " + reserva.getHabitacion().getNumero();
        tarjeta.debitar(montoAPagar, concepto);

        // 4. Acumular puntos por el monto pagado (100 puntos por cada $100.000 COP)
        int puntosObtenidos = cliente.getCuentaPuntos().acumularPorPago(montoAPagar, LocalDate.now(), concepto);

        // 5. Confirmar estado de la reserva
        reserva.getEstadoActual().confirmar(reserva);
        service.getReservas().add(reserva);

        // 6. Notificar a observadores
        service.getNotificadorReserva().notificarCreacion(reserva);

        return reserva;
    }

    public synchronized double cancelarReservaPorCliente(String codigoReserva) throws HotelException {
        Optional<Reserva> opt = service.buscarReservaPorCodigo(codigoReserva);
        if (opt.isEmpty()) {
            throw new ReservaException("No se encontró la reserva con código: " + codigoReserva);
        }

        Reserva reserva = opt.get();
        if (reserva.isFueReembolsado()) {
            throw new CancelacionInvalidaException("Esta reserva ya fue reembolsada con anterioridad.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLlegada = reserva.getFechaEntrada().atTime(15, 0); // Check-in oficial 15:00

        double porcentaje;
        if (reserva.tieneSeguroCancelacionFlexible()) {
            porcentaje = 100.0; // Cobertura especial adquirida
        } else {
            porcentaje = politicaCancelacionCliente.calcularPorcentajeReembolso(fechaLlegada, ahora);
        }

        double montoReembolso = (reserva.getTotalNeto() * porcentaje) / 100.0;

        // Efectuar cambio de estado
        reserva.getEstadoActual().cancelar(reserva, false);
        reserva.setFueReembolsado(true);
        reserva.setMontoReembolsado(montoReembolso);

        // Acreditar reembolso a la tarjeta virtual del cliente
        if (montoReembolso > 0) {
            reserva.getCliente().getTarjetaVirtual().reembolsar(montoReembolso,
                    "Reembolso (" + (int) porcentaje + "%) por cancelación de reserva " + reserva.getCodigo());
        }

        // Notificar observadores
        service.getNotificadorReserva().notificarCancelacion(reserva, montoReembolso);

        return montoReembolso;
    }

    public synchronized double cancelarReservaPorHotel(String codigoReserva, String motivo) throws HotelException {
        if (!SesionManager.getInstance().esAdministrador()) {
            throw new HotelException("Solo un administrador puede cancelar reservas directamente por el hotel.");
        }

        Optional<Reserva> opt = service.buscarReservaPorCodigo(codigoReserva);
        if (opt.isEmpty()) {
            throw new ReservaException("No se encontró la reserva con código: " + codigoReserva);
        }

        Reserva reserva = opt.get();
        if (reserva.isFueReembolsado()) {
            throw new CancelacionInvalidaException("La reserva ya tiene un reembolso previo.");
        }

        // Regla de Negocio 3.3: Cancelación por el hotel devuelve 100% incondicional
        double montoReembolso = reserva.getTotalNeto();

        reserva.getEstadoActual().cancelar(reserva, true);
        reserva.setFueReembolsado(true);
        reserva.setMontoReembolsado(montoReembolso);

        reserva.getCliente().getTarjetaVirtual().reembolsar(montoReembolso,
                "Reembolso 100% por cancelación administrativa del hotel. Motivo: " + motivo);

        service.getNotificadorReserva().notificarCancelacion(reserva, montoReembolso);

        return montoReembolso;
    }

    public synchronized void recargarTarjetaVirtual(String identificacionCliente, double monto, String conceptoAdmin) throws HotelException {
        if (!SesionManager.getInstance().esAdministrador()) {
            throw new HotelException("Acceso denegado: Únicamente un administrador puede autorizar y efectuar recargas.");
        }
        if (monto <= 0) {
            throw new HotelException("El monto a recargar debe ser un valor positivo.");
        }

        Optional<Cliente> opt = service.buscarClientePorIdentificacion(identificacionCliente);
        if (opt.isEmpty()) {
            throw new HotelException("No se encontró ningún cliente con identificación: " + identificacionCliente);
        }

        Cliente cliente = opt.get();
        cliente.getTarjetaVirtual().recargar(monto, conceptoAdmin != null ? conceptoAdmin : "Recarga autorizada por administración");
    }

    public void exportarReporte(PlantillaReporte plantilla, IExportadorReporte exportador,
                                LocalDate inicio, LocalDate fin, File archivoDestino) throws Exception {
        if (plantilla == null || exportador == null || archivoDestino == null) {
            throw new IllegalArgumentException("Parámetros de exportación no pueden ser nulos.");
        }
        DatosReporte datos = plantilla.generar(service.getReservas(), inicio, fin);
        exportador.exportar(datos, archivoDestino);
    }

    public HotelUQService getService() {
        return service;
    }
}
