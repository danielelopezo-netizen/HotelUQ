package co.edu.uniquindio.hotel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GeneradorDrawioLegible {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<mxfile host=\"app.diagrams.net\" modified=\"2026-11-20T02:00:00.000Z\" agent=\"HotelUQ Generator\" version=\"21.0.0\" type=\"device\">\n");

        // PAGINA 1: ARQUITECTURA GENERAL Y SISTEMA COMPLETO
        sb.append("  <diagram id=\"page_general\" name=\"1. Vista General del Sistema\">\n");
        sb.append("    <mxGraphModel dx=\"2000\" dy=\"1400\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\"3600\" pageHeight=\"2400\" math=\"0\" shadow=\"0\">\n");
        sb.append("      <root>\n");
        sb.append("        <mxCell id=\"0\" />\n");
        sb.append("        <mxCell id=\"1\" parent=\"0\" />\n");

        // Encabezado
        sb.append(crearBanner("ban_gen", 50, 30, 3500, 60,
                "HOTELUQ - SISTEMA INTEGRAL DE GESTIÓN HOTELERA | DIAGRAMA GENERAL DE ARQUITECTURA Y CLASES UML",
                "Universidad del Quindío - Programación II | Arquitectura MVC, Principios SOLID y 10+ Patrones GoF"));

        // CONTENEDOR 1: MODELO DEL DOMINIO
        sb.append(crearContenedor("pack_dominio", 60, 120, 1100, 1380,
                "PAQUETE: co.edu.uniquindio.hotel.model (Entidades del Dominio)", "#EBF3FB", "#2563EB"));

        // Clases dentro de dominio
        sb.append(crearClaseUml("c_persona", 100, 180, 280, 140,
                "&laquo;abstract&raquo;<br><b>Persona</b>",
                "# identificacion: String<br># nombre: String<br># email: String<br># telefono: String<br># password: String",
                "+ getIdentificacion(): String<br>+ getNombre(): String<br>+ getEmail(): String",
                "#DBEAFE", "#1D4ED8"));

        sb.append(crearClaseUml("c_cliente", 100, 370, 280, 130,
                "<b>Cliente</b>",
                "- tarjetaVirtual: TarjetaVirtual<br>- cuentaPuntos: CuentaPuntos",
                "+ getTarjetaVirtual(): TarjetaVirtual<br>+ getCuentaPuntos(): CuentaPuntos",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("c_admin", 430, 180, 240, 110,
                "<b>Administrador</b>",
                "- cargo: String",
                "+ getCargo(): String",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("e_cli_per", "c_cliente", "c_persona", "herencia", ""));
        sb.append(crearConector("e_adm_per", "c_admin", "c_persona", "herencia", ""));

        // Tarjeta Virtual y Movimientos
        sb.append(crearClaseUml("c_tarjeta", 100, 550, 300, 160,
                "<b>TarjetaVirtual</b>",
                "- numeroTarjeta: String<br>- saldo: double<br>- activa: boolean<br>- movimientos: List&lt;MovimientoTarjeta&gt;",
                "+ recargar(monto: double, det: String): void<br>+ debitar(monto: double, cto: String): void<br>+ reembolsar(monto: double, mot: String): void<br>+ getSaldo(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("c_movimiento", 100, 760, 300, 150,
                "<b>MovimientoTarjeta</b>",
                "- id: String<br>- fechaHora: LocalDateTime<br>- tipo: TipoMovimiento<br>- monto: double<br>- descripcion: String<br>- saldoPosterior: double",
                "+ getId(): String<br>+ getMonto(): double<br>+ getTipo(): TipoMovimiento",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("e_cli_tar", "c_cliente", "c_tarjeta", "composicion", "1", "1"));
        sb.append(crearConector("e_tar_mov", "c_tarjeta", "c_movimiento", "composicion", "1", "0..*"));

        // Cuenta Puntos y Registro
        sb.append(crearClaseUml("c_puntos", 450, 550, 300, 160,
                "<b>CuentaPuntos</b>",
                "- totalPuntos: int<br>- registros: List&lt;RegistroPuntos&gt;",
                "+ acumularPuntos(monto: double): int<br>+ redimirPuntosFIFO(cantidad: int): double<br>+ limpiarPuntosVencidos(): int<br>+ getTotalPuntos(): int",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("c_reg_puntos", 450, 760, 300, 150,
                "<b>RegistroPuntos</b>",
                "- id: String<br>- cantidadInicial: int<br>- cantidadDisponible: int<br>- fechaObtencion: LocalDate<br>- fechaVencimiento: LocalDate",
                "+ estaVencido(hoy: LocalDate): boolean<br>+ consumir(cant: int): int<br>+ getCantidadDisponible(): int",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("e_cli_pts", "c_cliente", "c_puntos", "composicion", "1", "1"));
        sb.append(crearConector("e_pts_reg", "c_puntos", "c_reg_puntos", "composicion", "1", "0..*"));

        // Jerarquia Habitacion
        sb.append(crearClaseUml("c_hab", 740, 180, 380, 170,
                "&laquo;abstract&raquo;<br><b>Habitacion</b>",
                "# numero: String<br># tipo: TipoHabitacion<br># precioBasePorNoche: double<br># capacidadMaxima: int<br># amenidades: List&lt;String&gt;",
                "+ {abstract} calcularPrecioPorNoche(): double<br>+ getNumero(): String<br>+ getTipo(): TipoHabitacion<br>+ getCapacidadMaxima(): int",
                "#DBEAFE", "#1D4ED8"));

        sb.append(crearClaseUml("c_hab_est", 820, 390, 280, 90,
                "<b>HabitacionEstandar</b>",
                "",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("c_hab_fam", 820, 510, 280, 90,
                "<b>HabitacionFamiliar</b>",
                "",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("c_hab_sui", 820, 630, 280, 90,
                "<b>HabitacionSuite</b>",
                "",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("c_hab_pre", 820, 750, 280, 90,
                "<b>HabitacionPresidencial</b>",
                "",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("e_hab_est", "c_hab_est", "c_hab", "herencia", ""));
        sb.append(crearConector("e_hab_fam", "c_hab_fam", "c_hab", "herencia", ""));
        sb.append(crearConector("e_hab_sui", "c_hab_sui", "c_hab", "herencia", ""));
        sb.append(crearConector("e_hab_pre", "c_hab_pre", "c_hab", "herencia", ""));

        // Reserva Central
        sb.append(crearClaseUml("c_reserva", 300, 970, 480, 230,
                "<b>Reserva</b>",
                "- codigoReserva: String<br>- cliente: Cliente<br>- habitacion: Habitacion<br>- fechaInicio: LocalDate<br>- fechaFin: LocalDate<br>- numeroHuespedes: int<br>- servicios: List&lt;ComponenteServicio&gt;<br>- estado: EstadoReserva<br>- costoTotal: double<br>- seguroCancelacionFlexible: boolean",
                "+ calcularTotal(): double<br>+ confirmar(): void<br>+ cancelarPorCliente(): double<br>+ cancelarPorHotel(): double<br>+ completar(): void<br>+ getCostoTotal(): double",
                "#FEF08A", "#854D0E"));

        sb.append(crearConector("e_res_cli", "c_reserva", "c_cliente", "asociacion", "0..*", "1"));
        sb.append(crearConector("e_res_hab", "c_reserva", "c_hab", "asociacion", "0..*", "1"));

        // CONTENEDOR 2: PATRONES CREACIONALES
        sb.append(crearContenedor("pack_creacional", 1210, 120, 750, 420,
                "PATRONES CREACIONALES: Factory Method, Builder, Singleton", "#FEF3C7", "#D97706"));

        sb.append(crearClaseUml("c_factory", 1240, 180, 320, 140,
                "&laquo;Factory Method&raquo;<br><b>HabitacionFactory</b>",
                "",
                "+ {static} crearHabitacion(tipo: TipoHabitacion, num: String, tarifa: double): Habitacion",
                "#FFFBEB", "#B45309"));

        sb.append(crearClaseUml("c_builder", 1600, 180, 330, 230,
                "&laquo;Builder&raquo;<br><b>ReservaBuilder</b>",
                "- reserva: Reserva",
                "+ paraCliente(c: Cliente): ReservaBuilder<br>+ enHabitacion(h: Habitacion): ReservaBuilder<br>+ fechas(ini: LocalDate, fin: LocalDate): ReservaBuilder<br>+ huespedes(cant: int): ReservaBuilder<br>+ conServicio(s: ComponenteServicio): ReservaBuilder<br>+ conSeguroFlexible(s: boolean): ReservaBuilder<br>+ build(): Reserva",
                "#FFFBEB", "#B45309"));

        sb.append(crearClaseUml("c_sesion", 1240, 350, 320, 150,
                "&laquo;Singleton&raquo;<br><b>SesionManager</b>",
                "- {static} instancia: SesionManager<br>- usuarioAutenticado: Persona",
                "+ {static} getInstance(): SesionManager<br>+ setUsuarioAutenticado(p: Persona): void<br>+ getUsuarioAutenticado(): Persona<br>+ cerrarSesion(): void",
                "#FFFBEB", "#B45309"));

        sb.append(crearConector("e_fac_hab", "c_factory", "c_hab", "dependencia", "crea"));
        sb.append(crearConector("e_bld_res", "c_builder", "c_reserva", "dependencia", "construye"));

        // CONTENEDOR 3: PATRONES ESTRUCTURALES
        sb.append(crearContenedor("pack_estructural", 2000, 120, 1500, 420,
                "PATRONES ESTRUCTURALES: Composite, Decorator, Adapter, Facade", "#E6F4EA", "#107C41"));

        // Composite de Servicios
        sb.append(crearClaseUml("c_comp_serv", 2030, 180, 320, 130,
                "&laquo;interface - Component&raquo;<br><b>ComponenteServicio</b>",
                "",
                "+ getNombre(): String<br>+ getPrecio(): double<br>+ getDescripcion(): String",
                "#E8F5E9", "#1B5E20"));

        sb.append(crearClaseUml("c_serv_simple", 2030, 350, 280, 130,
                "&laquo;Leaf&raquo;<br><b>ServicioSimple</b>",
                "- nombre: String<br>- precio: double<br>- descripcion: String",
                "+ getNombre(): String<br>+ getPrecio(): double",
                "#FFFFFF", "#1B5E20"));

        sb.append(crearClaseUml("c_paq_serv", 2350, 350, 320, 150,
                "&laquo;Composite&raquo;<br><b>PaqueteServiciosComposite</b>",
                "- nombre: String<br>- descuentoPorcentaje: double<br>- componentes: List&lt;ComponenteServicio&gt;",
                "+ agregar(s: ComponenteServicio): void<br>+ getPrecio(): double",
                "#FFFFFF", "#1B5E20"));

        sb.append(crearConector("e_simp_comp", "c_serv_simple", "c_comp_serv", "realizacion", ""));
        sb.append(crearConector("e_paq_comp", "c_paq_serv", "c_comp_serv", "realizacion", ""));
        sb.append(crearConector("e_paq_hijos", "c_paq_serv", "c_comp_serv", "agregacion", "1", "1..*"));

        // Decorator de Reserva
        sb.append(crearClaseUml("c_res_decor", 2720, 180, 340, 140,
                "&laquo;Decorator&raquo;<br><b>DecoradorReserva</b>",
                "# reservaDecorada: IDetalleReserva",
                "+ calcularCosto(): double<br>+ getDetalle(): String",
                "#E8F5E9", "#1B5E20"));

        sb.append(crearClaseUml("c_seguro_flex", 2720, 350, 340, 120,
                "&laquo;ConcreteDecorator&raquo;<br><b>SeguroCancelacionFlexibleDecorator</b>",
                "- TASA_SEGURO: double = 0.05",
                "+ calcularCosto(): double<br>+ permiteReembolsoTotal(): boolean",
                "#FFFFFF", "#1B5E20"));

        sb.append(crearConector("e_seg_dec", "c_seguro_flex", "c_res_decor", "herencia", ""));

        // Adapter de Reportes
        sb.append(crearClaseUml("c_iadapter", 3110, 180, 350, 130,
                "&laquo;interface - Target&raquo;<br><b>IExportadorReporte</b>",
                "",
                "+ exportarReporte(titulo: String, lineas: List&lt;String&gt;, archivo: File): void",
                "#E8F5E9", "#1B5E20"));

        sb.append(crearClaseUml("c_txt_adap", 3110, 340, 170, 100,
                "<b>ReporteTxtAdapter</b>",
                "",
                "+ exportarReporte(...): void",
                "#FFFFFF", "#1B5E20"));

        sb.append(crearClaseUml("c_pdf_adap", 3300, 340, 160, 100,
                "<b>ReportePdfAdapter</b>",
                "",
                "+ exportarReporte(...): void",
                "#FFFFFF", "#1B5E20"));

        sb.append(crearConector("e_txt_iad", "c_txt_adap", "c_iadapter", "realizacion", ""));
        sb.append(crearConector("e_pdf_iad", "c_pdf_adap", "c_iadapter", "realizacion", ""));

        // CONTENEDOR 4: PATRONES DE COMPORTAMIENTO
        sb.append(crearContenedor("pack_comportamiento", 1210, 580, 2290, 480,
                "PATRONES DE COMPORTAMIENTO: Strategy (Cancelación), State (Reserva), Observer (Auditoría), Template Method (Reportes)",
                "#F3E8FF", "#7E22CE"));

        // Strategy
        sb.append(crearClaseUml("c_istrat", 1240, 650, 320, 130,
                "&laquo;interface - Strategy&raquo;<br><b>PoliticaCancelacionStrategy</b>",
                "",
                "+ calcularReembolso(reserva: Reserva, horasAnticipacion: long): double",
                "#F5F3FF", "#6B21A8"));

        sb.append(crearClaseUml("c_strat_cli", 1240, 820, 320, 140,
                "<b>PoliticaCancelacionClienteStrategy</b>",
                "",
                "+ calcularReembolso(...): double<br>&gt; 72h: 100%<br>24-72h: 80%<br>&lt; 24h: 0%",
                "#FFFFFF", "#6B21A8"));

        sb.append(crearClaseUml("c_strat_hot", 1240, 980, 320, 110,
                "<b>PoliticaCancelacionHotelStrategy</b>",
                "",
                "+ calcularReembolso(...): double<br>Cancelacion Hotel = 100%",
                "#FFFFFF", "#6B21A8"));

        sb.append(crearConector("e_scli_istr", "c_strat_cli", "c_istrat", "realizacion", ""));
        sb.append(crearConector("e_shot_istr", "c_strat_hot", "c_istrat", "realizacion", ""));

        // State
        sb.append(crearClaseUml("c_istate", 1610, 650, 320, 130,
                "&laquo;interface - State&raquo;<br><b>EstadoReserva</b>",
                "",
                "+ confirmar(r: Reserva): void<br>+ cancelar(r: Reserva): void<br>+ completar(r: Reserva): void",
                "#F5F3FF", "#6B21A8"));

        sb.append(crearClaseUml("c_st_pen", 1610, 820, 150, 80, "<b>EstadoPendiente</b>", "", "+ confirmar()<br>+ cancelar()", "#FFFFFF", "#6B21A8"));
        sb.append(crearClaseUml("c_st_con", 1780, 820, 150, 80, "<b>EstadoConfirmada</b>", "", "+ cancelar()<br>+ completar()", "#FFFFFF", "#6B21A8"));
        sb.append(crearClaseUml("c_st_can", 1610, 930, 150, 80, "<b>EstadoCancelada</b>", "", "+ toString(): String", "#FFFFFF", "#6B21A8"));
        sb.append(crearClaseUml("c_st_com", 1780, 930, 150, 80, "<b>EstadoCompletada</b>", "", "+ toString(): String", "#FFFFFF", "#6B21A8"));

        sb.append(crearConector("e_spen_ist", "c_st_pen", "c_istate", "realizacion", ""));
        sb.append(crearConector("e_scon_ist", "c_st_con", "c_istate", "realizacion", ""));
        sb.append(crearConector("e_scan_ist", "c_st_can", "c_istate", "realizacion", ""));
        sb.append(crearConector("e_scom_ist", "c_st_com", "c_istate", "realizacion", ""));

        // Observer
        sb.append(crearClaseUml("c_iobs", 1990, 650, 300, 120,
                "&laquo;interface - Observer&raquo;<br><b>IReservaObserver</b>",
                "",
                "+ notificarCambioReserva(r: Reserva, evento: String): void",
                "#F5F3FF", "#6B21A8"));

        sb.append(crearClaseUml("c_sub_notif", 1990, 810, 300, 140,
                "&laquo;Subject&raquo;<br><b>NotificadorReserva</b>",
                "- observadores: List&lt;IReservaObserver&gt;",
                "+ suscribir(o: IReservaObserver): void<br>+ desuscribir(o: IReservaObserver): void<br>+ notificar(r: Reserva, ev: String): void",
                "#FFFFFF", "#6B21A8"));

        sb.append(crearClaseUml("c_obs_aud", 1990, 980, 300, 110,
                "<b>RegistroAuditoriaObserver</b>",
                "- bitacora: List&lt;String&gt;",
                "+ notificarCambioReserva(...): void<br>+ getBitacora(): List&lt;String&gt;",
                "#FFFFFF", "#6B21A8"));

        sb.append(crearConector("e_aud_iobs", "c_obs_aud", "c_iobs", "realizacion", ""));
        sb.append(crearConector("e_notif_iobs", "c_sub_notif", "c_iobs", "agregacion", "1", "0..*"));

        // Template Method
        sb.append(crearClaseUml("c_itemp", 2350, 650, 360, 170,
                "&laquo;abstract - Template&raquo;<br><b>PlantillaReporte</b>",
                "",
                "+ generarReporte(datos: List): Reporte<br># {abstract} filtrarDatos(d: List): List<br># {abstract} formatearFilas(d: List): List&lt;String&gt;<br># {abstract} calcularTotales(d: List): String",
                "#F5F3FF", "#6B21A8"));

        sb.append(crearClaseUml("c_rep_ing", 2350, 860, 360, 110,
                "<b>ReporteIngresosTemplate</b>",
                "",
                "# filtrarDatos(d: List): List<br># formatearFilas(d: List): List&lt;String&gt;<br># calcularTotales(d: List): String",
                "#FFFFFF", "#6B21A8"));

        sb.append(crearClaseUml("c_rep_ocu", 2350, 990, 360, 110,
                "<b>ReporteOcupacionTemplate</b>",
                "",
                "# filtrarDatos(d: List): List<br># formatearFilas(d: List): List&lt;String&gt;<br># calcularTotales(d: List): String",
                "#FFFFFF", "#6B21A8"));

        sb.append(crearConector("e_ing_temp", "c_rep_ing", "c_itemp", "herencia", ""));
        sb.append(crearConector("e_ocu_temp", "c_rep_ocu", "c_itemp", "herencia", ""));

        // CONTENEDOR 5: FACHADA Y SERVICIO CENTRAL
        sb.append(crearContenedor("pack_fachada", 1210, 1100, 1500, 400,
                "SERVICIOS Y FACHADA (Controladores y Orquestación)", "#FEE2E2", "#DC2626"));

        sb.append(crearClaseUml("c_facade", 1240, 1160, 440, 290,
                "&laquo;Facade&raquo;<br><b>HotelFachada</b>",
                "- hotelService: HotelUQService<br>- sesionManager: SesionManager",
                "+ iniciarSesion(email: String, pass: String): Persona<br>+ registrarCliente(cli: Cliente): void<br>+ buscarHabitacionesDisponibles(ini: LocalDate, fin: LocalDate): List&lt;Habitacion&gt;<br>+ crearReserva(...): Reserva<br>+ cancelarReserva(cod: String, porHotel: boolean): double<br>+ recargarTarjeta(email: String, monto: double): void<br>+ exportarReporte(tipo: String, formato: String, f: File): void",
                "#FFF1F2", "#BE123C"));

        sb.append(crearClaseUml("c_service", 1750, 1160, 440, 310,
                "&laquo;Singleton - Service&raquo;<br><b>HotelUQService</b>",
                "- usuarios: Map&lt;String, Persona&gt;<br>- habitaciones: Map&lt;String, Habitacion&gt;<br>- reservas: List&lt;Reserva&gt;<br>- servicios: List&lt;ComponenteServicio&gt;<br>- promociones: List&lt;Promocion&gt;<br>- notificador: NotificadorReserva",
                "+ {static} getInstance(): HotelUQService<br>+ agregarReserva(r: Reserva): void<br>+ obtenerHabitacionesDisponibles(...): List&lt;Habitacion&gt;<br>+ procesarPagoReserva(r: Reserva): void",
                "#FFF1F2", "#BE123C"));

        sb.append(crearConector("e_fac_serv", "c_facade", "c_service", "asociacion", "1", "1"));
        sb.append(crearConector("e_res_stat", "c_reserva", "c_istate", "asociacion", "1", "1"));
        sb.append(crearConector("e_res_serv", "c_reserva", "c_comp_serv", "agregacion", "1", "0..*"));

        sb.append("      </root>\n");
        sb.append("    </mxGraphModel>\n");
        sb.append("  </diagram>\n");

        // PAGINA 2: MODELO DEL DOMINIO EN DETALLE
        sb.append("  <diagram id=\"page_dominio\" name=\"2. Modelo del Dominio Detallado\">\n");
        sb.append("    <mxGraphModel dx=\"1800\" dy=\"1200\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\"2400\" pageHeight=\"1800\" math=\"0\" shadow=\"0\">\n");
        sb.append("      <root>\n");
        sb.append("        <mxCell id=\"0\" />\n");
        sb.append("        <mxCell id=\"1\" parent=\"0\" />\n");

        sb.append(crearBanner("ban_dom", 40, 20, 2300, 60,
                "HOTELUQ - MODELO DEL DOMINIO (co.edu.uniquindio.hotel.model)",
                "Gestión de Usuarios, Tarjeta Virtual, Billetera de Puntos FIFO, Tipos de Habitación y Reservas"));

        sb.append(crearClaseUml("d_persona", 60, 120, 320, 160,
                "&laquo;abstract&raquo;<br><b>Persona</b>",
                "# identificacion: String<br># nombre: String<br># email: String<br># telefono: String<br># password: String",
                "+ getIdentificacion(): String<br>+ getNombre(): String<br>+ getEmail(): String<br>+ validarPassword(pass: String): boolean",
                "#DBEAFE", "#1D4ED8"));

        sb.append(crearClaseUml("d_cliente", 60, 360, 320, 140,
                "<b>Cliente</b>",
                "- tarjetaVirtual: TarjetaVirtual<br>- cuentaPuntos: CuentaPuntos",
                "+ getTarjetaVirtual(): TarjetaVirtual<br>+ getCuentaPuntos(): CuentaPuntos",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("d_admin", 440, 120, 280, 120,
                "<b>Administrador</b>",
                "- cargo: String",
                "+ getCargo(): String<br>+ autorizarOperacion(): boolean",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("d_e_cli_per", "d_cliente", "d_persona", "herencia", ""));
        sb.append(crearConector("d_e_adm_per", "d_admin", "d_persona", "herencia", ""));

        sb.append(crearClaseUml("d_tarjeta", 60, 580, 340, 180,
                "<b>TarjetaVirtual</b>",
                "- numeroTarjeta: String<br>- saldo: double<br>- activa: boolean<br>- movimientos: List&lt;MovimientoTarjeta&gt;",
                "+ recargar(monto: double, detalle: String): void<br>+ debitar(monto: double, concepto: String): void<br>+ reembolsar(monto: double, motivo: String): void<br>+ getSaldo(): double<br>+ isActiva(): boolean",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("d_mov", 60, 830, 340, 160,
                "<b>MovimientoTarjeta</b>",
                "- id: String<br>- fechaHora: LocalDateTime<br>- tipo: TipoMovimiento<br>- monto: double<br>- descripcion: String<br>- saldoPosterior: double",
                "+ getId(): String<br>+ getMonto(): double<br>+ getTipo(): TipoMovimiento<br>+ getSaldoPosterior(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("d_e_cli_tar", "d_cliente", "d_tarjeta", "composicion", "1", "1"));
        sb.append(crearConector("d_e_tar_mov", "d_tarjeta", "d_mov", "composicion", "1", "0..*"));

        sb.append(crearClaseUml("d_puntos", 450, 580, 340, 180,
                "<b>CuentaPuntos</b>",
                "- totalPuntos: int<br>- registros: List&lt;RegistroPuntos&gt;",
                "+ acumularPuntos(montoPagado: double): int<br>+ redimirPuntosFIFO(puntosARedimir: int): double<br>+ limpiarPuntosVencidos(): int<br>+ getTotalPuntos(): int<br>+ getValorEnPesos(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("d_reg_pts", 450, 830, 340, 160,
                "<b>RegistroPuntos</b>",
                "- id: String<br>- cantidadInicial: int<br>- cantidadDisponible: int<br>- fechaObtencion: LocalDate<br>- fechaVencimiento: LocalDate",
                "+ estaVencido(hoy: LocalDate): boolean<br>+ consumir(cantidad: int): int<br>+ getCantidadDisponible(): int<br>+ getFechaVencimiento(): LocalDate",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("d_e_cli_pts", "d_cliente", "d_puntos", "composicion", "1", "1"));
        sb.append(crearConector("d_e_pts_reg", "d_puntos", "d_reg_pts", "composicion", "1", "0..*"));

        sb.append(crearClaseUml("d_hab", 870, 120, 420, 200,
                "&laquo;abstract&raquo;<br><b>Habitacion</b>",
                "# numero: String<br># tipo: TipoHabitacion<br># precioBasePorNoche: double<br># capacidadMaxima: int<br># amenidades: List&lt;String&gt;",
                "+ {abstract} calcularPrecioPorNoche(): double<br>+ getNumero(): String<br>+ getTipo(): TipoHabitacion<br>+ getPrecioBasePorNoche(): double<br>+ getCapacidadMaxima(): int<br>+ admiteHuespedes(cant: int): boolean",
                "#DBEAFE", "#1D4ED8"));

        sb.append(crearClaseUml("d_hab_est", 870, 380, 300, 100,
                "<b>HabitacionEstandar</b>",
                "Capacidad: 2 personas",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("d_hab_fam", 1200, 380, 300, 100,
                "<b>HabitacionFamiliar</b>",
                "Capacidad: 5 personas",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("d_hab_sui", 870, 520, 300, 100,
                "<b>HabitacionSuite</b>",
                "Capacidad: 4 personas",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearClaseUml("d_hab_pre", 1200, 520, 300, 100,
                "<b>HabitacionPresidencial</b>",
                "Capacidad: 6 personas",
                "+ calcularPrecioPorNoche(): double",
                "#FFFFFF", "#1D4ED8"));

        sb.append(crearConector("d_e_h_est", "d_hab_est", "d_hab", "herencia", ""));
        sb.append(crearConector("d_e_h_fam", "d_hab_fam", "d_hab", "herencia", ""));
        sb.append(crearConector("d_e_h_sui", "d_hab_sui", "d_hab", "herencia", ""));
        sb.append(crearConector("d_e_h_pre", "d_hab_pre", "d_hab", "herencia", ""));

        // Reserva
        sb.append(crearClaseUml("d_reserva", 870, 700, 540, 280,
                "<b>Reserva</b>",
                "- codigoReserva: String<br>- cliente: Cliente<br>- habitacion: Habitacion<br>- fechaInicio: LocalDate<br>- fechaFin: LocalDate<br>- numeroHuespedes: int<br>- servicios: List&lt;ComponenteServicio&gt;<br>- estado: EstadoReserva<br>- costoTotal: double<br>- seguroCancelacionFlexible: boolean",
                "+ calcularTotal(): double<br>+ confirmar(): void<br>+ cancelarPorCliente(): double<br>+ cancelarPorHotel(): double<br>+ completar(): void<br>+ solapaCon(ini: LocalDate, fin: LocalDate): boolean<br>+ getCostoTotal(): double",
                "#FEF08A", "#854D0E"));

        sb.append(crearConector("d_e_res_cli", "d_reserva", "d_cliente", "asociacion", "0..*", "1"));
        sb.append(crearConector("d_e_res_hab", "d_reserva", "d_hab", "asociacion", "0..*", "1"));

        sb.append("      </root>\n");
        sb.append("    </mxGraphModel>\n");
        sb.append("  </diagram>\n");

        // PAGINA 3: PATRONES DE DISENO DETALLADOS
        sb.append("  <diagram id=\"page_patrones\" name=\"3. Patrones de Diseño GoF\">\n");
        sb.append("    <mxGraphModel dx=\"1800\" dy=\"1200\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\"2800\" pageHeight=\"2000\" math=\"0\" shadow=\"0\">\n");
        sb.append("      <root>\n");
        sb.append("        <mxCell id=\"0\" />\n");
        sb.append("        <mxCell id=\"1\" parent=\"0\" />\n");

        sb.append(crearBanner("ban_pat", 40, 20, 2700, 60,
                "HOTELUQ - CATALOGO COMPLETO DE PATRONES DE DISEÑO GoF",
                "3 Creacionales (Factory Method, Builder, Singleton) | 4 Estructurales (Composite, Decorator, Adapter, Facade) | 4 Comportamiento (Strategy, State, Observer, Template Method)"));

        // Fila 1: Creacionales
        sb.append(crearClaseUml("p_factory", 60, 120, 360, 140,
                "&laquo;Creational: Factory Method&raquo;<br><b>HabitacionFactory</b>",
                "",
                "+ {static} crearHabitacion(tipo: TipoHabitacion, num: String, tarifa: double): Habitacion",
                "#FEF3C7", "#D97706"));

        sb.append(crearClaseUml("p_builder", 470, 120, 380, 220,
                "&laquo;Creational: Builder&raquo;<br><b>ReservaBuilder</b>",
                "- reserva: Reserva",
                "+ paraCliente(c: Cliente): ReservaBuilder<br>+ enHabitacion(h: Habitacion): ReservaBuilder<br>+ fechas(ini: LocalDate, fin: LocalDate): ReservaBuilder<br>+ huespedes(cant: int): ReservaBuilder<br>+ conServicio(s: ComponenteServicio): ReservaBuilder<br>+ conSeguroFlexible(s: boolean): ReservaBuilder<br>+ build(): Reserva",
                "#FEF3C7", "#D97706"));

        sb.append(crearClaseUml("p_sesion", 900, 120, 340, 150,
                "&laquo;Creational: Singleton&raquo;<br><b>SesionManager</b>",
                "- {static} instancia: SesionManager<br>- usuarioAutenticado: Persona",
                "+ {static} getInstance(): SesionManager<br>+ setUsuarioAutenticado(p: Persona): void<br>+ getUsuarioAutenticado(): Persona<br>+ cerrarSesion(): void",
                "#FEF3C7", "#D97706"));

        // Fila 2: Estructurales
        sb.append(crearClaseUml("p_comp_serv", 60, 420, 360, 130,
                "&laquo;Structural: Composite - Component&raquo;<br><b>ComponenteServicio</b>",
                "",
                "+ getNombre(): String<br>+ getPrecio(): double<br>+ getDescripcion(): String",
                "#E6F4EA", "#107C41"));

        sb.append(crearClaseUml("p_serv_leaf", 60, 600, 300, 120,
                "&laquo;Structural: Leaf&raquo;<br><b>ServicioSimple</b>",
                "- nombre: String<br>- precio: double",
                "+ getNombre(): String<br>+ getPrecio(): double",
                "#FFFFFF", "#107C41"));

        sb.append(crearClaseUml("p_serv_comp", 400, 600, 340, 140,
                "&laquo;Structural: Composite&raquo;<br><b>PaqueteServiciosComposite</b>",
                "- componentes: List&lt;ComponenteServicio&gt;<br>- descuentoPorcentaje: double",
                "+ agregar(s: ComponenteServicio): void<br>+ getPrecio(): double",
                "#FFFFFF", "#107C41"));

        sb.append(crearConector("p_e_leaf_c", "p_serv_leaf", "p_comp_serv", "realizacion", ""));
        sb.append(crearConector("p_e_comp_c", "p_serv_comp", "p_comp_serv", "realizacion", ""));
        sb.append(crearConector("p_e_comp_elem", "p_serv_comp", "p_comp_serv", "agregacion", "1", "1..*"));

        sb.append(crearClaseUml("p_decor", 820, 420, 360, 130,
                "&laquo;Structural: Decorator&raquo;<br><b>DecoradorReserva</b>",
                "# reservaDecorada: IDetalleReserva",
                "+ calcularCosto(): double<br>+ getDetalle(): String",
                "#E6F4EA", "#107C41"));

        sb.append(crearClaseUml("p_seg_flex", 820, 600, 360, 120,
                "&laquo;Structural: ConcreteDecorator&raquo;<br><b>SeguroCancelacionFlexibleDecorator</b>",
                "- TASA_SEGURO: double = 0.05",
                "+ calcularCosto(): double<br>+ permiteReembolsoTotal(): boolean",
                "#FFFFFF", "#107C41"));

        sb.append(crearConector("p_e_seg_dec", "p_seg_flex", "p_decor", "herencia", ""));

        sb.append(crearClaseUml("p_iadap", 1250, 420, 360, 120,
                "&laquo;Structural: Adapter - Target&raquo;<br><b>IExportadorReporte</b>",
                "",
                "+ exportarReporte(titulo: String, lineas: List&lt;String&gt;, f: File): void",
                "#E6F4EA", "#107C41"));

        sb.append(crearClaseUml("p_txt_ad", 1250, 590, 170, 90,
                "<b>ReporteTxtAdapter</b>",
                "",
                "+ exportarReporte(...): void",
                "#FFFFFF", "#107C41"));

        sb.append(crearClaseUml("p_pdf_ad", 1440, 590, 170, 90,
                "<b>ReportePdfAdapter</b>",
                "",
                "+ exportarReporte(...): void",
                "#FFFFFF", "#107C41"));

        sb.append(crearConector("p_e_txt_ad", "p_txt_ad", "p_iadap", "realizacion", ""));
        sb.append(crearConector("p_e_pdf_ad", "p_pdf_ad", "p_iadap", "realizacion", ""));

        sb.append(crearClaseUml("p_facade", 1680, 420, 420, 240,
                "&laquo;Structural: Facade&raquo;<br><b>HotelFachada</b>",
                "- hotelService: HotelUQService<br>- sesionManager: SesionManager",
                "+ iniciarSesion(email: String, pass: String): Persona<br>+ registrarCliente(cli: Cliente): void<br>+ buscarHabitacionesDisponibles(ini: LocalDate, fin: LocalDate): List&lt;Habitacion&gt;<br>+ crearReserva(...): Reserva<br>+ cancelarReserva(cod: String, porHotel: boolean): double<br>+ recargarTarjeta(email: String, monto: double): void",
                "#E6F4EA", "#107C41"));

        // Fila 3: Comportamiento
        sb.append(crearClaseUml("p_istrat", 60, 830, 360, 120,
                "&laquo;Behavioral: Strategy&raquo;<br><b>PoliticaCancelacionStrategy</b>",
                "",
                "+ calcularReembolso(reserva: Reserva, horasAnticipacion: long): double",
                "#F3E8FF", "#7E22CE"));

        sb.append(crearClaseUml("p_strat_cli", 60, 1000, 360, 140,
                "<b>PoliticaCancelacionClienteStrategy</b>",
                "",
                "+ calcularReembolso(...): double<br>&gt; 72h: 100% | 24-72h: 80% | &lt; 24h: 0%",
                "#FFFFFF", "#7E22CE"));

        sb.append(crearClaseUml("p_strat_hot", 60, 1180, 360, 100,
                "<b>PoliticaCancelacionHotelStrategy</b>",
                "",
                "+ calcularReembolso(...): double (100% garantizado)",
                "#FFFFFF", "#7E22CE"));

        sb.append(crearConector("p_e_st_cli", "p_strat_cli", "p_istrat", "realizacion", ""));
        sb.append(crearConector("p_e_st_hot", "p_strat_hot", "p_istrat", "realizacion", ""));

        sb.append(crearClaseUml("p_istate", 480, 830, 360, 120,
                "&laquo;Behavioral: State&raquo;<br><b>EstadoReserva</b>",
                "",
                "+ confirmar(r: Reserva): void<br>+ cancelar(r: Reserva): void<br>+ completar(r: Reserva): void",
                "#F3E8FF", "#7E22CE"));

        sb.append(crearClaseUml("p_state_pen", 480, 1000, 170, 80, "<b>EstadoPendiente</b>", "", "+ confirmar()<br>+ cancelar()", "#FFFFFF", "#7E22CE"));
        sb.append(crearClaseUml("p_state_con", 670, 1000, 170, 80, "<b>EstadoConfirmada</b>", "", "+ cancelar()<br>+ completar()", "#FFFFFF", "#7E22CE"));
        sb.append(crearClaseUml("p_state_can", 480, 1110, 170, 80, "<b>EstadoCancelada</b>", "", "+ toString(): String", "#FFFFFF", "#7E22CE"));
        sb.append(crearClaseUml("p_state_com", 670, 1110, 170, 80, "<b>EstadoCompletada</b>", "", "+ toString(): String", "#FFFFFF", "#7E22CE"));

        sb.append(crearConector("p_e_st_p", "p_state_pen", "p_istate", "realizacion", ""));
        sb.append(crearConector("p_e_st_c", "p_state_con", "p_istate", "realizacion", ""));
        sb.append(crearConector("p_e_st_x", "p_state_can", "p_istate", "realizacion", ""));
        sb.append(crearConector("p_e_st_k", "p_state_com", "p_istate", "realizacion", ""));

        sb.append(crearClaseUml("p_iobs", 900, 830, 340, 120,
                "&laquo;Behavioral: Observer&raquo;<br><b>IReservaObserver</b>",
                "",
                "+ notificarCambioReserva(r: Reserva, evento: String): void",
                "#F3E8FF", "#7E22CE"));

        sb.append(crearClaseUml("p_obs_sub", 900, 990, 340, 140,
                "&laquo;Subject&raquo;<br><b>NotificadorReserva</b>",
                "- observadores: List&lt;IReservaObserver&gt;",
                "+ suscribir(o: IReservaObserver): void<br>+ desuscribir(o: IReservaObserver): void<br>+ notificar(r: Reserva, ev: String): void",
                "#FFFFFF", "#7E22CE"));

        sb.append(crearClaseUml("p_obs_aud", 900, 1160, 340, 110,
                "<b>RegistroAuditoriaObserver</b>",
                "- bitacora: List&lt;String&gt;",
                "+ notificarCambioReserva(...): void",
                "#FFFFFF", "#7E22CE"));

        sb.append(crearConector("p_e_aud_obs", "p_obs_aud", "p_iobs", "realizacion", ""));
        sb.append(crearConector("p_e_sub_obs", "p_obs_sub", "p_iobs", "agregacion", "1", "0..*"));

        sb.append(crearClaseUml("p_itemp", 1310, 830, 380, 160,
                "&laquo;Behavioral: Template Method&raquo;<br><b>PlantillaReporte</b>",
                "",
                "+ generarReporte(datos: List): Reporte<br># {abstract} filtrarDatos(d: List): List<br># {abstract} formatearFilas(d: List): List&lt;String&gt;<br># {abstract} calcularTotales(d: List): String",
                "#F3E8FF", "#7E22CE"));

        sb.append(crearClaseUml("p_rep_ing", 1310, 1030, 380, 110,
                "<b>ReporteIngresosTemplate</b>",
                "",
                "# filtrarDatos(d: List): List<br># formatearFilas(d: List): List&lt;String&gt;<br># calcularTotales(d: List): String",
                "#FFFFFF", "#7E22CE"));

        sb.append(crearClaseUml("p_rep_ocu", 1310, 1170, 380, 110,
                "<b>ReporteOcupacionTemplate</b>",
                "",
                "# filtrarDatos(d: List): List<br># formatearFilas(d: List): List&lt;String&gt;<br># calcularTotales(d: List): String",
                "#FFFFFF", "#7E22CE"));

        sb.append(crearConector("p_e_ing_t", "p_rep_ing", "p_itemp", "herencia", ""));
        sb.append(crearConector("p_e_ocu_t", "p_rep_ocu", "p_itemp", "herencia", ""));

        sb.append("      </root>\n");
        sb.append("    </mxGraphModel>\n");
        sb.append("  </diagram>\n");

        sb.append("</mxfile>\n");

        File destino = new File("docs/DIAGRAMA_CLASES_HOTELUQ.drawio");
        destino.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(destino, StandardCharsets.UTF_8)) {
            fw.write(sb.toString());
            System.out.println("Archivo Draw.io generado exitosamente en: " + destino.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String crearBanner(String id, int x, int y, int w, int h, String titulo, String subtitulo) {
        String val = String.format("<div style=\"text-align: center;\"><b style=\"font-size: 16px; color: #FFFFFF;\">%s</b><br><span style=\"font-size: 12px; color: #D1D5DB;\">%s</span></div>",
                titulo, subtitulo);
        return String.format("        <mxCell id=\"%s\" value=\"%s\" style=\"rounded=1;whiteSpace=wrap;html=1;arcSize=4;fillColor=#0F172A;strokeColor=#1E293B;align=center;verticalAlign=middle;\" vertex=\"1\" parent=\"1\">\n" +
                "          <mxGeometry x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" as=\"geometry\" />\n" +
                "        </mxCell>\n", id, val.replace("\"", "&quot;"), x, y, w, h);
    }

    private static String crearContenedor(String id, int x, int y, int w, int h, String titulo, String fill, String stroke) {
        String val = String.format("<div style=\"font-weight: bold; font-size: 13px; color: %s; padding-left: 6px;\">%s</div>", stroke, titulo);
        return String.format("        <mxCell id=\"%s\" value=\"%s\" style=\"swimlane;startSize=30;rounded=1;arcSize=4;whiteSpace=wrap;html=1;fillColor=%s;strokeColor=%s;strokeWidth=2;verticalAlign=top;\" vertex=\"1\" parent=\"1\">\n" +
                "          <mxGeometry x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" as=\"geometry\" />\n" +
                "        </mxCell>\n", id, val.replace("\"", "&quot;"), fill, stroke, x, y, w, h);
    }

    private static String crearClaseUml(String id, int x, int y, int w, int h, String cabecera, String atributos, String metodos, String fill, String stroke) {
        StringBuilder val = new StringBuilder();
        val.append("<div style=\"text-align: center; padding-top: 4px; padding-bottom: 4px;\">").append(cabecera).append("</div>");
        if (atributos != null && !atributos.trim().isEmpty()) {
            val.append("<hr style=\"margin: 4px 0; border: 0.5px solid ").append(stroke).append(";\">");
            val.append("<div style=\"text-align: left; padding: 2px 4px; font-size: 11px; line-height: 1.35;\">").append(atributos).append("</div>");
        }
        if (metodos != null && !metodos.trim().isEmpty()) {
            val.append("<hr style=\"margin: 4px 0; border: 0.5px solid ").append(stroke).append(";\">");
            val.append("<div style=\"text-align: left; padding: 2px 4px; font-size: 11px; line-height: 1.35;\">").append(metodos).append("</div>");
        }

        return String.format("        <mxCell id=\"%s\" value=\"%s\" style=\"rounded=1;whiteSpace=wrap;html=1;arcSize=6;fillColor=%s;strokeColor=%s;strokeWidth=2;align=left;verticalAlign=top;overflow=hidden;\" vertex=\"1\" parent=\"1\">\n" +
                "          <mxGeometry x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" as=\"geometry\" />\n" +
                "        </mxCell>\n", id, val.toString().replace("\"", "&quot;"), fill, stroke, x, y, w, h);
    }

    private static String crearConector(String id, String source, String target, String tipo, String label) {
        return crearConector(id, source, target, tipo, label, "");
    }

    private static String crearConector(String id, String source, String target, String tipo, String sourceLabel, String targetLabel) {
        String edgeStyle = "edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeWidth=1.8;";
        if ("herencia".equalsIgnoreCase(tipo)) {
            edgeStyle += "endArrow=block;endFill=0;strokeColor=#1E40AF;";
        } else if ("realizacion".equalsIgnoreCase(tipo)) {
            edgeStyle += "dashed=1;endArrow=block;endFill=0;strokeColor=#047857;";
        } else if ("composicion".equalsIgnoreCase(tipo)) {
            edgeStyle += "startArrow=diamond;startFill=1;endArrow=none;strokeColor=#1F2937;";
        } else if ("agregacion".equalsIgnoreCase(tipo)) {
            edgeStyle += "startArrow=diamond;startFill=0;endArrow=none;strokeColor=#1F2937;";
        } else if ("dependencia".equalsIgnoreCase(tipo)) {
            edgeStyle += "dashed=1;endArrow=open;endFill=0;strokeColor=#B45309;";
        } else {
            edgeStyle += "endArrow=classic;strokeColor=#374151;";
        }

        String labelText = (sourceLabel != null && !sourceLabel.isEmpty()) ? sourceLabel : "";

        return String.format("        <mxCell id=\"%s\" value=\"%s\" style=\"%s\" edge=\"1\" parent=\"1\" source=\"%s\" target=\"%s\">\n" +
                "          <mxGeometry relative=\"1\" as=\"geometry\" />\n" +
                "        </mxCell>\n", id, labelText, edgeStyle, source, target);
    }
}
