package co.edu.uniquindio.hotel;

import co.edu.uniquindio.hotel.model.*;
import co.edu.uniquindio.hotel.patterns.behavioral.*;
import co.edu.uniquindio.hotel.patterns.creational.HabitacionFactory;
import co.edu.uniquindio.hotel.patterns.creational.ReservaBuilder;
import co.edu.uniquindio.hotel.patterns.structural.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestRunner {

    private static int pruebasExitosas = 0;
    private static int pruebasFallidas = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("               EJECUCIÓN DE PRUEBAS DE VERIFICACIÓN HOTELUQ                     ");
        System.out.println("================================================================================");

        ejecutarPrueba("Creacional - Factory Method", () -> {
            Habitacion fam = HabitacionFactory.crearHabitacion(TipoHabitacion.FAMILIAR, "205", 2, 220000, "Familiar");
            if (!(fam instanceof HabitacionFamiliar) || fam.getCapacidadMaxima() != 5) {
                throw new AssertionError("Fallo en creación con Factory Method");
            }
        });

        ejecutarPrueba("Creacional - Builder Reserva", () -> {
            Cliente c = new Cliente("111", "Juan", "j@uq.edu", "300", "123");
            Habitacion hab = HabitacionFactory.crearHabitacion(TipoHabitacion.SUITE, "301", 3, 350000, "Suite");
            Reserva res = new ReservaBuilder()
                    .paraCliente(c)
                    .enHabitacion(hab)
                    .conFechas(LocalDate.now().plusDays(2), LocalDate.now().plusDays(5))
                    .conHuespedes(2)
                    .build();
            if (res.getNumeroNoches() != 3 || res.getTotalNeto() != 350000 * 3) {
                throw new AssertionError("Fallo en totales del Builder");
            }
        });

        ejecutarPrueba("Estructural - Composite Servicios", () -> {
            ServicioSimple s1 = new ServicioSimple("S1", "Spa", "Spa", 80000);
            ServicioSimple s2 = new ServicioSimple("S2", "Cena", "Cena", 120000);
            PaqueteServiciosComposite pkg = new PaqueteServiciosComposite("P1", "Relax", "Spa + Cena", 20.0);
            pkg.agregarComponente(s1);
            pkg.agregarComponente(s2);
            if (pkg.getPrecio() != 160000.0) {
                throw new AssertionError("El cálculo del paquete Composite no coincide: " + pkg.getPrecio());
            }
        });

        ejecutarPrueba("Estructural - Decorator Reserva", () -> {
            Cliente c = new Cliente("111", "Juan", "j@uq.edu", "300", "123");
            Habitacion hab = HabitacionFactory.crearHabitacion(TipoHabitacion.ESTANDAR, "101", 1, 100000, "Estándar");
            Reserva res = new ReservaBuilder()
                    .paraCliente(c)
                    .enHabitacion(hab)
                    .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
                    .conHuespedes(1)
                    .build();
            IDetalleReserva decorada = new SeguroCancelacionFlexibleDecorator(res);
            if (!decorada.tieneSeguroCancelacionFlexible() || decorada.getPrecioFinal() != (res.getTotalNeto() + 35000.0)) {
                throw new AssertionError("Fallo en Decorator");
            }
        });

        ejecutarPrueba("Negocio - Puntos HotelUQ (Acumulación y FIFO)", () -> {
            CuentaPuntos cp = new CuentaPuntos("111");
            cp.acreditarPuntosDirectos(200, LocalDate.now().minusMonths(10), "Antiguos");
            cp.acreditarPuntosDirectos(300, LocalDate.now().minusMonths(1), "Nuevos");
            if (cp.getPuntosValidos(LocalDate.now()) != 500) throw new AssertionError("Puntos inválidos");

            cp.redimirPuntos(250, LocalDate.now());
            if (cp.getPuntosValidos(LocalDate.now()) != 250) throw new AssertionError("Deducción FIFO incorrecta");
            if (cp.getRegistros().get(0).getPuntosDisponibles() != 0) throw new AssertionError("No consumió el lote antiguo primero");
        });

        ejecutarPrueba("Comportamiento - Strategy Cancelación", () -> {
            PoliticaCancelacionStrategy st = new PoliticaCancelacionClienteStrategy();
            if (st.calcularPorcentajeReembolso(LocalDateTime.now().plusHours(80), LocalDateTime.now()) != 100.0) {
                throw new AssertionError("Reembolso >72h incorrecto");
            }
            if (st.calcularPorcentajeReembolso(LocalDateTime.now().plusHours(40), LocalDateTime.now()) != 80.0) {
                throw new AssertionError("Reembolso 24-72h incorrecto");
            }
            if (st.calcularPorcentajeReembolso(LocalDateTime.now().plusHours(5), LocalDateTime.now()) != 0.0) {
                throw new AssertionError("Reembolso <24h incorrecto");
            }
            PoliticaCancelacionStrategy hotelSt = new PoliticaCancelacionHotelStrategy();
            if (hotelSt.calcularPorcentajeReembolso(LocalDateTime.now().plusHours(2), LocalDateTime.now()) != 100.0) {
                throw new AssertionError("Reembolso por hotel debe ser 100%");
            }
        });

        ejecutarPrueba("Regla Adicional 1 - Descuento Larga Estadía (>7 noches)", () -> {
            Cliente c = new Cliente("111", "Juan", "j@uq.edu", "300", "123");
            Habitacion hab = HabitacionFactory.crearHabitacion(TipoHabitacion.ESTANDAR, "101", 1, 100000, "Estándar");
            Reserva res = new ReservaBuilder()
                    .paraCliente(c)
                    .enHabitacion(hab)
                    .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(11)) // 10 noches
                    .conHuespedes(1)
                    .build();
            if (res.getDescuentoLargaEstadia() != 1000000 * 0.10) {
                throw new AssertionError("No aplicó el 10% de descuento por larga estadía");
            }
        });

        ejecutarPrueba("Regla Adicional 2 - Control de Capacidad Máxima", () -> {
            Cliente c = new Cliente("111", "Juan", "j@uq.edu", "300", "123");
            Habitacion hab = HabitacionFactory.crearHabitacion(TipoHabitacion.ESTANDAR, "101", 1, 100000, "Estándar");
            boolean lanzoExcepcion = false;
            try {
                new ReservaBuilder()
                        .paraCliente(c)
                        .enHabitacion(hab)
                        .conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
                        .conHuespedes(5) // Máximo 2
                        .build();
            } catch (Exception e) {
                lanzoExcepcion = true;
            }
            if (!lanzoExcepcion) throw new AssertionError("Debió lanzar excepción por exceso de capacidad");
        });

        ejecutarPrueba("Estructural - Adapter & Template: Generación Reportes TXT y PDF", () -> {
            PlantillaReporte template = new ReporteIngresosTemplate();
            Cliente c = new Cliente("111", "Juan", "j@uq.edu", "300", "123");
            Habitacion hab = HabitacionFactory.crearHabitacion(TipoHabitacion.SUITE, "301", 3, 350000, "Suite");
            Reserva res = new ReservaBuilder().paraCliente(c).enHabitacion(hab).conFechas(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)).conHuespedes(2).build();

            DatosReporte datos = template.generar(List.of(res), LocalDate.now(), LocalDate.now().plusMonths(1));
            File fTxt = new File("target/Reporte_Prueba.txt");
            File fPdf = new File("target/Reporte_Prueba.pdf");

            new ReporteTxtAdapter().exportar(datos, fTxt);
            new ReportePdfAdapter().exportar(datos, fPdf);

            if (!fTxt.exists() || fTxt.length() == 0) throw new AssertionError("Fallo exportación TXT");
            if (!fPdf.exists() || fPdf.length() == 0) throw new AssertionError("Fallo exportación PDF");
        });

        System.out.println("================================================================================");
        System.out.println("RESULTADO FINAL: " + pruebasExitosas + " EXITOSAS | " + pruebasFallidas + " FALLIDAS");
        System.out.println("================================================================================");
    }

    private static void ejecutarPrueba(String nombre, AccionPrueba accion) {
        try {
            accion.ejecutar();
            System.out.println("  [PASS] " + nombre);
            pruebasExitosas++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] " + nombre + " -> " + t.getMessage());
            pruebasFallidas++;
        }
    }

    @FunctionalInterface
    interface AccionPrueba {
        void ejecutar() throws Throwable;
    }
}

