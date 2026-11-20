package co.edu.uniquindio.hotel.service;

import co.edu.uniquindio.hotel.model.*;
import co.edu.uniquindio.hotel.patterns.behavioral.*;
import co.edu.uniquindio.hotel.patterns.creational.HabitacionFactory;
import co.edu.uniquindio.hotel.patterns.structural.ComponenteServicio;
import co.edu.uniquindio.hotel.patterns.structural.PaqueteServiciosComposite;
import co.edu.uniquindio.hotel.patterns.structural.ServicioSimple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HotelUQService {
    private static HotelUQService instance;

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Habitacion> habitaciones = new ArrayList<>();
    private final List<ComponenteServicio> catalogoServicios = new ArrayList<>();
    private final List<Promocion> promociones = new ArrayList<>();
    private final List<BeneficioPuntos> catalogoBeneficios = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final NotificadorReserva notificadorReserva = new NotificadorReserva();
    private final RegistroAuditoriaObserver auditoriaObserver = new RegistroAuditoriaObserver();

    private HotelUQService() {
        notificadorReserva.suscribir(auditoriaObserver);
        inicializarDatosSemilla();
    }

    public static synchronized HotelUQService getInstance() {
        if (instance == null) {
            instance = new HotelUQService();
        }
        return instance;
    }

    private void inicializarDatosSemilla() {
        // 1. Administrador por defecto
        Administrador admin = new Administrador("1001", "Admin General HotelUQ", "admin@hoteluq.com", "3001234567", "admin123", "Gerente General");
        administradores.add(admin);

        // 2. Clientes con saldo y puntos iniciales
        Cliente c1 = new Cliente("1094901001", "Carlos Mendoza", "carlos@gmail.com", "3101234567", "123");
        c1.getTarjetaVirtual().recargar(1500000, "Depósito inicial de apertura de cuenta");
        // Puntos con diferentes fechas (para demostrar política FIFO y vencimiento)
        c1.getCuentaPuntos().acreditarPuntosDirectos(300, LocalDate.now().minusMonths(6), "Puntos promocionales bienvenida");
        c1.getCuentaPuntos().acreditarPuntosDirectos(200, LocalDate.now().minusMonths(2), "Puntos por consumo de restaurante");
        c1.getCuentaPuntos().acreditarPuntosDirectos(100, LocalDate.now().minusYears(1).minusDays(5), "Puntos caducados (hace > 1 año)");
        clientes.add(c1);

        Cliente c2 = new Cliente("1094901002", "Laura Restrepo", "laura@gmail.com", "3119876543", "123");
        c2.getTarjetaVirtual().recargar(900000, "Recarga inicial en recepción");
        c2.getCuentaPuntos().acreditarPuntosDirectos(450, LocalDate.now().minusMonths(3), "Puntos acumulados estancia previa");
        clientes.add(c2);

        Cliente c3 = new Cliente("1094901003", "Andrés Salazar", "andres@gmail.com", "3123456789", "123");
        c3.getTarjetaVirtual().recargar(600000, "Depósito transferencia bancaria");
        c3.getCuentaPuntos().acreditarPuntosDirectos(150, LocalDate.now().minusMonths(1), "Puntos por reserva web");
        clientes.add(c3);

        // 3. Habitaciones (Usando Factory Method)
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.ESTANDAR, "101", 1, 130000, "Vista al jardín interior, aire acondicionado y WiFi."));
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.ESTANDAR, "102", 1, 130000, "Excelente iluminación natural, escritorio de trabajo."));
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.FAMILIAR, "201", 2, 240000, "Amplia habitación con dos camas Queen y balcón privado."));
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.FAMILIAR, "202", 2, 240000, "Equipada para familias con frigobar y sala pequeña."));
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.SUITE, "301", 3, 390000, "Jacuzzi con hidromasaje, cama King size y minibar premium."));
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.SUITE, "302", 3, 390000, "Vista panorámica a la cordillera, sala y baño de lujo."));
        habitaciones.add(HabitacionFactory.crearHabitacion(TipoHabitacion.PRESIDENCIAL, "401", 4, 780000, "Piso exclusivo, terraza privada, mayordomo y dos recámaras King."));

        // 4. Servicios y Paquetes (Usando Composite)
        ServicioSimple sDesayuno = new ServicioSimple("SRV-01", "Desayuno Buffet Gourmet", "Desayuno artesanal con productos de la región y café de origen.", 28000);
        ServicioSimple sSpa = new ServicioSimple("SRV-02", "Circuito Spa & Hidroterapia", "Sesión de 60 minutos con sauna, turco e hidromasaje relajante.", 75000);
        ServicioSimple sParqueadero = new ServicioSimple("SRV-03", "Parqueadero Cubierto 24h", "Plaza de parqueo monitoreada con vigilancia permanente.", 16000);
        ServicioSimple sTransporte = new ServicioSimple("SRV-04", "Transporte Aeropuerto VIP", "Traslado privado con chofer en camioneta ejecutiva.", 55000);
        ServicioSimple sCena = new ServicioSimple("SRV-05", "Cena Romántica 3 Tiempos", "Menú especial del chef maridado con vino de la casa.", 110000);

        catalogoServicios.add(sDesayuno);
        catalogoServicios.add(sSpa);
        catalogoServicios.add(sParqueadero);
        catalogoServicios.add(sTransporte);
        catalogoServicios.add(sCena);

        // Paquete Composite 1: Relajación Total
        PaqueteServiciosComposite pkgRelax = new PaqueteServiciosComposite("PKG-01", "Paquete Bienestar & Spa", "Incluye Spa, Desayuno Buffet y Cena Romántica con 15% de descuento.", 15.0);
        pkgRelax.agregarComponente(sSpa);
        pkgRelax.agregarComponente(sDesayuno);
        pkgRelax.agregarComponente(sCena);
        catalogoServicios.add(pkgRelax);

        // Paquete Composite 2: Ejecutivo Movilidad
        PaqueteServiciosComposite pkgEjecutivo = new PaqueteServiciosComposite("PKG-02", "Paquete Ejecutivo Confort", "Incluye Parqueadero 24h, Desayuno Buffet y Transporte Aeropuerto con 12% off.", 12.0);
        pkgEjecutivo.agregarComponente(sParqueadero);
        pkgEjecutivo.agregarComponente(sDesayuno);
        pkgEjecutivo.agregarComponente(sTransporte);
        catalogoServicios.add(pkgEjecutivo);

        // 5. Promociones
        promociones.add(new Promocion("PRM-01", "PROMO-FINDE", "Escapada Fin de Semana", 15.0, 3, null, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(6)));
        promociones.add(new Promocion("PRM-02", "PROMO-SUITE", "Experiencia Suite Premium", 20.0, 2, TipoHabitacion.SUITE, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(6)));
        promociones.add(new Promocion("PRM-03", "PROMO-VERANO", "Vacaciones de Temporada", 10.0, 1, null, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(8)));

        // 6. Beneficios de Puntos
        catalogoBeneficios.add(new BeneficioPuntos("BEN-01", "Bono Descuento Hospedaje", "Descuento de $25.000 COP en cualquier reserva.", 250, 25000, BeneficioPuntos.TipoBeneficio.DESCUENTO_RESERVA));
        catalogoBeneficios.add(new BeneficioPuntos("BEN-02", "Desayuno Buffet Gratuito", "Un desayuno buffet gourmet de cortesía.", 280, 28000, BeneficioPuntos.TipoBeneficio.SERVICIO_GRATIS));
        catalogoBeneficios.add(new BeneficioPuntos("BEN-03", "Bono Descuento Plus", "Descuento de $60.000 COP en la estadía.", 600, 60000, BeneficioPuntos.TipoBeneficio.DESCUENTO_RESERVA));
        catalogoBeneficios.add(new BeneficioPuntos("BEN-04", "Acceso Spa Relax", "Entrada individual al circuito de Spa.", 750, 75000, BeneficioPuntos.TipoBeneficio.SERVICIO_GRATIS));

        // 7. Reservas Semilla Históricas y Futuras para estadísticas
        // Reserva 1: Completada en el pasado
        Reserva r1 = new Reserva("res-001", "RES-10001", c1, habitaciones.get(0), LocalDate.now().minusDays(15), LocalDate.now().minusDays(12), 2);
        r1.agregarServicio(sDesayuno);
        r1.recalcularTotales();
        r1.setEstadoActual(new EstadoCompletada());
        r1.setFechaCreacion(LocalDateTime.now().minusDays(20));
        reservas.add(r1);

        // Reserva 2: Completada en Suite
        Reserva r2 = new Reserva("res-002", "RES-10002", c2, habitaciones.get(4), LocalDate.now().minusDays(8), LocalDate.now().minusDays(5), 2);
        r2.agregarServicio(pkgRelax);
        r2.recalcularTotales();
        r2.setEstadoActual(new EstadoCompletada());
        r2.setFechaCreacion(LocalDateTime.now().minusDays(15));
        reservas.add(r2);

        // Reserva 3: Activa Confirmada a futuro
        Reserva r3 = new Reserva("res-003", "RES-10003", c1, habitaciones.get(2), LocalDate.now().plusDays(5), LocalDate.now().plusDays(9), 4);
        r3.agregarServicio(sDesayuno);
        r3.agregarServicio(sParqueadero);
        r3.setPromocionAplicada(promociones.get(0));
        r3.recalcularTotales();
        r3.setEstadoActual(new EstadoConfirmada());
        r3.setFechaCreacion(LocalDateTime.now().minusDays(1));
        reservas.add(r3);
    }

    // Getters de colecciones sincronizadas
    public synchronized List<Cliente> getClientes() {
        return clientes;
    }

    public synchronized List<Administrador> getAdministradores() {
        return administradores;
    }

    public synchronized List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public synchronized List<ComponenteServicio> getCatalogoServicios() {
        return catalogoServicios;
    }

    public synchronized List<Promocion> getPromociones() {
        return promociones;
    }

    public synchronized List<BeneficioPuntos> getCatalogoBeneficios() {
        return catalogoBeneficios;
    }

    public synchronized List<Reserva> getReservas() {
        return reservas;
    }

    public NotificadorReserva getNotificadorReserva() {
        return notificadorReserva;
    }

    public RegistroAuditoriaObserver getAuditoriaObserver() {
        return auditoriaObserver;
    }

    // Métodos de búsqueda
    public synchronized Optional<Cliente> buscarClientePorIdentificacion(String id) {
        return clientes.stream().filter(c -> c.getIdentificacion().equalsIgnoreCase(id)).findFirst();
    }

    public synchronized Optional<Cliente> buscarClientePorEmail(String email) {
        return clientes.stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public synchronized Optional<Administrador> buscarAdministradorPorEmail(String email) {
        return administradores.stream().filter(a -> a.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public synchronized Optional<Habitacion> buscarHabitacionPorNumero(String numero) {
        return habitaciones.stream().filter(h -> h.getNumero().equalsIgnoreCase(numero)).findFirst();
    }

    public synchronized Optional<Reserva> buscarReservaPorCodigo(String codigo) {
        return reservas.stream().filter(r -> r.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }
}

