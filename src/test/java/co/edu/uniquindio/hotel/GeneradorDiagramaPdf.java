package co.edu.uniquindio.hotel;

import co.edu.uniquindio.hotel.patterns.structural.DatosReporte;
import co.edu.uniquindio.hotel.patterns.structural.ReportePdfAdapter;

import java.io.File;

public class GeneradorDiagramaPdf {
    public static void main(String[] args) throws Exception {
        DatosReporte datos = new DatosReporte("ESPECIFICACION FORMAL DEL DIAGRAMA DE CLASES UML", "Universidad del Quindio - PGII");
        datos.getEncabezadosColumnas().add("Modulo / Entidad");
        datos.getEncabezadosColumnas().add("Tipo");
        datos.getEncabezadosColumnas().add("Patron / Principio");
        datos.getEncabezadosColumnas().add("Relacion / Multiplicidad");

        datos.agregarFila("Persona -> Cliente, Admin", "Clase Abstracta", "Herencia / SRP", "Generalizacion (1 a N)");
        datos.agregarFila("Cliente *-- TarjetaVirtual", "Composicion Fuerte", "Encapsulamiento", "1 Cliente a 1 Tarjeta");
        datos.agregarFila("Cliente *-- CuentaPuntos", "Composicion Fuerte", "Algoritmo FIFO", "1 Cliente a 1 Cuenta");
        datos.agregarFila("Habitacion (Abstracta)", "Herencia Polimorfica", "LSP / OCP", "4 Subclases concretas");
        datos.agregarFila("HabitacionFactory", "Factoria Creacional", "Factory Method", "Crea instancias Habitacion");
        datos.agregarFila("ComponenteServicio", "Interface Estructural", "Composite Pattern", "Hojas y Compuestos");
        datos.agregarFila("ReservaBuilder", "Constructor Fluido", "Builder Pattern", "Construye Reserva paso a paso");
        datos.agregarFila("DecoradorReserva", "Envoltorio Dinamico", "Decorator Pattern", "Agrega Seguro / Servicios VIP");
        datos.agregarFila("EstadoReserva", "Interface de Estados", "State Pattern", "Pendiente, Confirmada, Cancelada");
        datos.agregarFila("PoliticaCancelacionStrategy", "Algoritmos Intercambiables", "Strategy Pattern", "Reembolsos 100%, 80%, 0%");
        datos.agregarFila("IExportadorReporte", "Adaptador Formatos", "Adapter Pattern", "Genera TXT y PDF nativo");
        datos.agregarFila("PlantillaReporte", "Esqueleto de Algoritmo", "Template Method", "Reportes Ingresos y Ocupacion");
        datos.agregarFila("HotelFachada", "Fachada Unificada", "Facade Pattern", "Punto unico para vistas JavaFX");

        datos.agregarTotal("Total de Patrones GoF", "10 Patrones Implementados");
        datos.agregarTotal("Patrones Creacionales", "3 (Factory, Builder, Singleton)");
        datos.agregarTotal("Patrones Estructurales", "4 (Composite, Decorator, Adapter, Facade)");
        datos.agregarTotal("Patrones de Comportamiento", "4 (Strategy, State, Observer, Template)");
        datos.agregarTotal("Archivo Draw.io editable", "docs/DIAGRAMA_CLASES_HOTELUQ.drawio");

        File destino = new File("docs/DIAGRAMA_CLASES_HOTELUQ.pdf");
        new ReportePdfAdapter().exportar(datos, destino);
        System.out.println("PDF del diagrama generado con exito en: " + destino.getAbsolutePath());
    }
}

