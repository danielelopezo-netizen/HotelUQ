package co.edu.uniquindio.hotel;

import co.edu.uniquindio.hotel.exceptions.HabitacionNoDisponibleException;
import co.edu.uniquindio.hotel.exceptions.PuntosInsuficientesException;
import co.edu.uniquindio.hotel.exceptions.ReservaException;
import co.edu.uniquindio.hotel.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.hotel.model.*;
import co.edu.uniquindio.hotel.patterns.behavioral.*;
import co.edu.uniquindio.hotel.patterns.creational.HabitacionFactory;
import co.edu.uniquindio.hotel.patterns.creational.ReservaBuilder;
import co.edu.uniquindio.hotel.patterns.structural.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HotelUQTest {

    private Cliente cliente;
    private Habitacion habitacionSuite;
    private Habitacion habitacionEstandar;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("1094909999", "Tester UQ", "tester@uq.edu.co", "3000000000", "pass123");
        cliente.getTarjetaVirtual().recargar(2000000, "Saldo de prueba para testing");

        // Factory Method
        habitacionSuite = HabitacionFactory.crearHabitacion(TipoHabitacion.SUITE, "305", 3, 350000, "Suite ejecutiva con jacuzzi");
        habitacionEstandar = HabitacionFactory.crearHabitacion(TipoHabitacion.ESTANDAR, "105", 1, 120000, "Habitación estándar confortable");
    }

    @Test
    @DisplayName("Creacional - Factory Method: Debe crear instancias concretas según TipoHabitacion")
    void testFactoryMethod() {
        Habitacion fam = HabitacionFactory.crearHabitacion(TipoHabitacion.FAMILIAR, "205", 2, 220000, "Familiar");
        assertTrue(fam instanceof HabitacionFamiliar);
        assertEquals(5, fam.getCapacidadMaxima());

        Habitacion pres = HabitacionFactory.crearHabitacion(TipoHabitacion.PRESIDENCIAL, "405", 4, 750000, "Presidencial");
        assertTrue(pres instanceof HabitacionPresidencial);
        assertEquals(6, pres.getCapacidadMaxima());
    }

    @Test
    @DisplayName("Creacional - Builder: Debe construir la reserva validando fechas y huéspedes")
    void testReservaBuilder() throws ReservaException {
        LocalDate entrada = LocalDate.now().plusDays(2);
        LocalDate salida = LocalDate.now().plusDays(5);

        Reserva reserva = new ReservaBuilder()
                .paraCliente(cliente)
                .enHabitacion(habitacionSuite)
                .conFechas(entrada, salida)
                .conHuespedes(2)
                .build();

        assertNotNull(reserva);
        assertEquals(3, reserva.getNumeroNoches());
        assertEquals(350000 * 3, reserva.getTotalNeto());
        assertEquals(TipoEstadoReserva.CONFIRMADA, reserva.getTipoEstado());
    }

    @Test
    @DisplayName("Estructural - Composite: El paquete compuesto debe sumar sus servicios y aplicar descuento")
    void testCompositeServicios() {
        ServicioSimple s1 = new ServicioSimple("S1", "Spa", "Spa", 80000);
        ServicioSimple s2 = new ServicioSimple("S2", "Cena", "Cena", 120000);

        PaqueteServiciosComposite paquete = new PaqueteServiciosComposite("P1", "Relax", "Spa + Cena", 20.0);
        paquete.agregarComponente(s1);
        paquete.agregarComponente(s2);

        assertEquals(200000.0, paquete.getSubtotalSinDescuento());
        // Con 20% de descuento -> 200.000 * 0.80 = 160.000
        assertEquals(160000.0, paquete.getPrecio());
    }

    @Test
    @DisplayName("Estructural - Decorator: Debe añadir coberturas y costos a la reserva")
    void testDecoratorReserva() throws ReservaException {
        Reserva base = new ReservaBuilder()
                .paraCliente(cliente)
                .enHabitacion(habitacionEstandar)
                .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
                .conHuespedes(2)
                .build();

        IDetalleReserva decorada = new SeguroCancelacionFlexibleDecorator(base);
        assertEquals(base.getTotalNeto() + SeguroCancelacionFlexibleDecorator.COSTO_SEGURO, decorada.getPrecioFinal());
        assertTrue(decorada.tieneSeguroCancelacionFlexible());
    }

    @Test
    @DisplayName("Negocio - Tarjeta Virtual: Débito, recarga y control de saldo insuficiente")
    void testTarjetaVirtual() throws Exception {
        TarjetaVirtual tv = cliente.getTarjetaVirtual();
        double saldoInicial = tv.getSaldo();

        tv.debitar(500000, "Pago de prueba");
        assertEquals(saldoInicial - 500000, tv.getSaldo());

        // Intentar sobregiro
        assertThrows(SaldoInsuficienteException.class, () -> tv.debitar(99999999, "Exceso"));
    }

    @Test
    @DisplayName("Negocio - Puntos HotelUQ: Acumulación y Redención estricta en orden FIFO")
    void testProgramaPuntosFIFO() throws PuntosInsuficientesException {
        CuentaPuntos cp = new CuentaPuntos(cliente.getIdentificacion());

        // Acumular 200 puntos hace 10 meses (vence en 2 meses)
        cp.acreditarPuntosDirectos(200, LocalDate.now().minusMonths(10), "Lote Antiguo");
        // Acumular 300 puntos hace 1 mes (vence en 11 meses)
        cp.acreditarPuntosDirectos(300, LocalDate.now().minusMonths(1), "Lote Nuevo");

        assertEquals(500, cp.getPuntosValidos(LocalDate.now()));

        // Redimir 250 puntos: debe agotar los 200 del lote antiguo y 50 del lote nuevo
        cp.redimirPuntos(250, LocalDate.now());

        assertEquals(250, cp.getPuntosValidos(LocalDate.now()));
        assertEquals(0, cp.getRegistros().get(0).getPuntosDisponibles());
        assertEquals(250, cp.getRegistros().get(1).getPuntosDisponibles());
    }

    @Test
    @DisplayName("Comportamiento - Strategy: Políticas de Reembolso según anticipación de cancelación")
    void testPoliticaCancelacion() {
        PoliticaCancelacionStrategy clienteStrategy = new PoliticaCancelacionClienteStrategy();
        LocalDateTime llegada = LocalDateTime.now().plusHours(80);

        // > 72 horas -> 100%
        assertEquals(100.0, clienteStrategy.calcularPorcentajeReembolso(llegada, LocalDateTime.now()));

        // Entre 24 y 72 horas -> 80%
        LocalDateTime llegada50h = LocalDateTime.now().plusHours(50);
        assertEquals(80.0, clienteStrategy.calcularPorcentajeReembolso(llegada50h, LocalDateTime.now()));

        // < 24 horas -> 0%
        LocalDateTime llegada10h = LocalDateTime.now().plusHours(10);
        assertEquals(0.0, clienteStrategy.calcularPorcentajeReembolso(llegada10h, LocalDateTime.now()));

        // Cancelación por el Hotel -> 100% incondicional (Regla 3.3)
        PoliticaCancelacionStrategy hotelStrategy = new PoliticaCancelacionHotelStrategy();
        assertEquals(100.0, hotelStrategy.calcularPorcentajeReembolso(llegada10h, LocalDateTime.now()));
    }

    @Test
    @DisplayName("Regla Adicional 1 - Descuento 10% por Larga Estadía (> 7 noches)")
    void testReglaAdicionalLargaEstadia() throws ReservaException {
        Reserva corta = new ReservaBuilder()
                .paraCliente(cliente)
                .enHabitacion(habitacionEstandar)
                .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)) // 3 noches
                .conHuespedes(1)
                .build();
        assertEquals(0.0, corta.getDescuentoLargaEstadia());

        Reserva larga = new ReservaBuilder()
                .paraCliente(cliente)
                .enHabitacion(habitacionEstandar)
                .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(11)) // 10 noches
                .conHuespedes(1)
                .build();
        assertTrue(larga.getDescuentoLargaEstadia() > 0.0);
        assertEquals(larga.getCostoHospedajeBruto() * 0.10, larga.getDescuentoLargaEstadia());
    }

    @Test
    @DisplayName("Regla Adicional 2 - Control de Capacidad Máxima de Huéspedes")
    void testReglaAdicionalCapacidadMaxima() {
        // Estándar permite máximo 2
        assertThrows(ReservaException.class, () -> {
            new ReservaBuilder()
                    .paraCliente(cliente)
                    .enHabitacion(habitacionEstandar)
                    .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
                    .conHuespedes(5) // Excede capacidad de 2
                    .build();
        });
    }

    @Test
    @DisplayName("Estructural - Adapter & Template Method: Exportación de Reportes TXT y PDF")
    void testExportacionReportes() throws Exception {
        PlantillaReporte plantilla = new ReporteIngresosTemplate();
        Reserva r = new ReservaBuilder()
                .paraCliente(cliente)
                .enHabitacion(habitacionSuite)
                .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4))
                .conHuespedes(2)
                .build();

        DatosReporte datos = plantilla.generar(List.of(r), LocalDate.now(), LocalDate.now().plusMonths(1));
        assertNotNull(datos);
        assertTrue(datos.getCantidadRegistros() > 0);

        // Exportar a TXT
        File txtFile = File.createTempFile("test_reporte", ".txt");
        txtFile.deleteOnExit();
        new ReporteTxtAdapter().exportar(datos, txtFile);
        assertTrue(txtFile.exists() && txtFile.length() > 0);

        // Exportar a PDF estándar
        File pdfFile = File.createTempFile("test_reporte", ".pdf");
        pdfFile.deleteOnExit();
        new ReportePdfAdapter().exportar(datos, pdfFile);
        assertTrue(pdfFile.exists() && pdfFile.length() > 0);
    }
}

