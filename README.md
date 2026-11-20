# 🌿 HotelUQ - Sistema Integral de Gestión Hotelera

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![Patrones GoF](https://img.shields.io/badge/Patrones-GoF%20(10+)-green.svg)]()
[![SOLID](https://img.shields.io/badge/Principios-SOLID-brightgreen.svg)]()

Proyecto Final para la asignatura **Programación II** de la **Universidad del Quindío**.  
Plataforma moderna en Java y JavaFX que gestiona de manera integral habitaciones, reservas de huéspedes, contratación de servicios adicionales simples y en paquete, tarjeta virtual de prepago con saldo y movimientos, programa de fidelización de puntos con caducidad anual (FIFO), cancelación con políticas escalonadas de reembolso, analítica visual con gráficos JavaFX y generación de reportes operativos exportables en **TXT** y **PDF**.

---

## 👥 Credenciales de Acceso Rápido (Datos Semilla)

Al iniciar la aplicación, dispone de botones de acceso rápido para pruebas inmediatas:

| Perfil | Correo Electrónico | Contraseña | Rol / Descripción |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@hoteluq.com` | `admin123` | Control total, recargas de saldo, cancelación hotel (100%), gráficos y reportes. |
| **Cliente 1** | `carlos@gmail.com` | `123` | Carlos Mendoza: Saldo \$1.500.000 COP, 500 puntos (demostración FIFO y vencimientos). |
| **Cliente 2** | `laura@gmail.com` | `123` | Laura Restrepo: Saldo \$900.000 COP, 450 puntos. |
| **Cliente 3** | `andres@gmail.com` | `123` | Andrés Salazar: Saldo \$600.000 COP, 150 puntos. |

---

## 🏛️ Arquitectura del Software (MVC y Principios SOLID)

El sistema implementa una separación estricta entre la capa de interfaz, la lógica de negocio y el modelo de datos bajo el patrón **MVC**:

```
co.edu.uniquindio.hotel
├── model/                     # Entidades del dominio y enumeraciones
├── exceptions/                # Excepciones propias del dominio
├── patterns/
│   ├── creational/            # Factory Method, Builder, Singleton
│   ├── structural/            # Composite, Decorator, Adapter, Facade
│   └── behavioral/            # Strategy, State, Observer, Template Method
├── service/                   # Repositorio en memoria y orquestación
└── controller/                # Controladores JavaFX (Login, Cliente, Admin)
```

### Cumplimiento Riguroso de Principios SOLID
1. **SRP (Single Responsibility):** Cada clase posee una única responsabilidad bien acotada (`TarjetaVirtual` gestiona saldos y transacciones contables, `CuentaPuntos` gestiona vencimientos y algoritmos FIFO, `ReservaBuilder` construye reservas, etc.).
2. **OCP (Open/Closed):** Nuevas políticas de cancelación o promociones se extienden implementando `PoliticaCancelacionStrategy` o `PromocionStrategy` sin modificar las clases de negocio existentes.
3. **LSP (Liskov Substitution):** Subclases como `HabitacionEstandar`, `HabitacionFamiliar`, `HabitacionSuite` y `HabitacionPresidencial` sustituyen a `Habitacion` en cálculos de tarifas y chequeo de disponibilidad sin efectos secundarios.
4. **ISP (Interface Segregation):** Interfaces pequeñas y cohesivas: `IExportadorReporte`, `IReservaObserver`, `ComponenteServicio`, `IDetalleReserva`.
5. **DIP (Dependency Inversion):** Los módulos de alto nivel (como `HotelFachada` o los controladores) dependen de abstracciones (`IExportadorReporte`, `PoliticaCancelacionStrategy`, etc.) y no de implementaciones concretas.

---

## 🧩 Patrones de Diseño GoF Implementados

### 1. Patrones Creacionales (3)
* **Factory Method:** `HabitacionFactory.crearHabitacion(...)` crea instancias según `TipoHabitacion` con sus capacidades, amenidades y tarifas.
* **Builder:** `ReservaBuilder` construye paso a paso reservas garantizando la integridad de fechas, selección de servicios, validación de cupos y descuentos.
* **Singleton:** `SesionManager` y `HotelUQService` como puntos únicos y sincronizados de acceso al estado de la aplicación.

### 2. Patrones Estructurales (4)
* **Composite:** `ComponenteServicio` con hojas (`ServicioSimple`: Spa, Desayuno, Parqueadero) y compuestos (`PaqueteServiciosComposite`: Paquete Bienestar & Spa, Paquete Ejecutivo) que calculan polimórficamente precios con descuento integrado.
* **Decorator:** `DecoradorReserva` con `SeguroCancelacionFlexibleDecorator` (otorga 100% de reembolso sin importar la fecha) y `ServicioVIPDecorator`.
* **Adapter:** `IExportadorReporte` adaptando los datos del hotel a archivos `.txt` (`ReporteTxtAdapter`) y a documentos formales `.pdf` (`ReportePdfAdapter`).
* **Facade (Fachada):** `HotelFachada` expone una interfaz unificada y simple para los controladores JavaFX, ocultando la complejidad de disponibilidad, pagos, acumulación de puntos y auditoría.

### 3. Patrones de Comportamiento (3 + 1)
* **Strategy:** `PoliticaCancelacionStrategy` con implementaciones `PoliticaCancelacionClienteStrategy` (>72h = 100%, 24-72h = 80%, <24h = 0%) y `PoliticaCancelacionHotelStrategy` (100% garantizado).
* **State:** `EstadoReserva` (`EstadoPendiente`, `EstadoConfirmada`, `EstadoCancelada`, `EstadoCompletada`) controlando transiciones válidas.
* **Observer:** `NotificadorReserva` notifica a `RegistroAuditoriaObserver` cada vez que una reserva es creada, cancelada o completada.
* **Template Method:** `PlantillaReporte` estandariza el encabezado, filtrado, armado de filas y consolidación de totales para reportes financieros y de ocupación.

---

## 📋 Reglas de Negocio Implementadas

1. **Disponibilidad:** No se admiten reservas con solapamiento de fechas en una misma habitación.
2. **Tarjeta Virtual:** Solo el Administrador puede recargar fondos. Antes de pagar se verifica tarjeta activa y saldo suficiente.
3. **Reembolsos Escalonados:**
   * Cancelación por Cliente:
     * $>$ 72 horas antes del check-in: **100%** de devolución.
     * $24$ a $72$ horas antes: **80%** de devolución.
     * $<$ 24 horas antes: **0%** de devolución.
   * Cancelación directa por el Hotel: **100%** incondicional devuelto a la tarjeta virtual.
   * Todo reembolso retorna a la tarjeta virtual y no puede reembolsarse más de una vez.
4. **Programa de Puntos HotelUQ:**
   * 100 puntos por cada \$100.000 COP pagados.
   * Vigencia de 1 año calendario desde su obtención.
   * Consumo bajo política **FIFO** de los puntos con vencimiento más cercano.
5. **Regla Adicional 1 (Larga Estadía):** Estadías de más de 7 noches obtienen un **10% de descuento adicional** automático en la tarifa base de habitación.
6. **Regla Adicional 2 (Capacidad Máxima y Control No-Show):** Control estricto del límite de ocupantes por tipo de habitación (Estándar: 2, Suite: 4, Familiar: 5, Presidencial: 6).

---

## 📊 Reportes Operativos

* **Reportes Visuales (JavaFX):**
  * `BarChart`: Comparación de ingresos por tipo de habitación.
  * `PieChart`: Distribución porcentual de ocupación y reservas.
  * `LineChart`: Evolución temporal de ingresos a lo largo de las fechas.
* **Reportes Exportables:**
  * Generación en **TXT** y **PDF** con título, fecha, registros detallados y totales consolidados.

---

## 🚀 Cómo Ejecutar el Proyecto

### Opción 1: Con Maven y JavaFX
```bash
mvn clean compile
mvn javafx:run
```

### Opción 2: Ejecución de la Suite de Pruebas Unitarias
El proyecto incluye un script listo para ejecutar la batería de pruebas automatizadas:
```bash
run_tests.bat
```

---

## 🌳 Estructura de Ramas Git
Para dar cumplimiento a los entregables del curso (commits atómicos y ramas temáticas por requerimiento):
* `main`: Rama de producción estable.
* `feature/analisis-y-diseno`: Requisitos funcionales codificados y arquitectura.
* `feature/modelo-dominio-excepciones`: Entidades, tarjeta virtual, puntos y excepciones.
* `feature/patrones-creacionales`: Factory Method y Builder.
* `feature/patrones-estructurales`: Composite, Decorator, Adapter y Facade.
* `feature/patrones-comportamiento`: Strategy, State, Observer y Template Method.
* `feature/interfaz-javafx-mvc`: Vistas FXML, estilos CSS y controladores.
* `feature/pruebas-unitarias-reportes`: Tests automatizados y exportación de reportes TXT/PDF.

