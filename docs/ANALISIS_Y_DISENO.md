# HotelUQ - Sistema Integral de Gestión Hotelera
## Documento de Análisis, Diseño y Requisitos de Software
**Curso:** Programación II  
**Institución:** Universidad del Quindío  
**Tecnologías:** Java 17, JavaFX, Principios SOLID, Patrones de Diseño GoF, Maven

---

## 1. Actores del Sistema

| Actor | Descripción |
| :--- | :--- |
| **Cliente** | Usuario final que consulta disponibilidad de habitaciones según fechas, realiza reservas, contrata servicios individuales o paquetes, redime promociones y puntos, gestiona su tarjeta virtual y puede solicitar cancelaciones de acuerdo a las políticas de anticipación. |
| **Administrador** | Usuario con permisos de gestión global que administra clientes, habitaciones, tipos de habitación, servicios adicionales, paquetes, promociones, recargas de saldo de tarjetas virtuales, cancelación forzosa con reembolso total por parte del hotel, análisis estadístico y exportación de reportes operativos (TXT/PDF). |

---

## 2. Requisitos Funcionales (Codificados)

### Módulo de Autenticación y Perfiles
* **RF-001 - Registro e Inicio de Sesión de Clientes:** El sistema debe permitir a nuevos clientes registrarse con sus datos personales (identificación, nombre, correo, teléfono) y autenticarse en la aplicación.
* **RF-002 - Autenticación de Administrador:** El sistema debe permitir el acceso administrativo mediante credenciales predeterminadas para gestionar la plataforma.

### Módulo de Habitaciones y Disponibilidad
* **RF-003 - Gestión de Tipos de Habitación y Habitaciones:** El administrador podrá crear, modificar y listar tipos de habitaciones (Estándar, Familiar, Suite, Presidencial) con sus respectivas características y tarifas base por noche.
* **RF-004 - Consulta de Habitaciones Disponibles:** El cliente podrá seleccionar una fecha de entrada (*check-in*) y salida (*check-out*), validando que la salida sea posterior a la entrada, y visualizar únicamente las habitaciones que no tengan reservas confirmadas en conflicto en ese periodo.

### Módulo de Reservas y Servicios
* **RF-005 - Creación y Configuración de Reserva:** El cliente podrá seleccionar una habitación disponible y construir su reserva especificando el número de huéspedes y noches.
* **RF-006 - Contratación de Servicios Adicionales y Paquetes:** El sistema permitirá agregar servicios individuales (desayuno, transporte, spa, parqueadero) o paquetes compuestos de servicios a la reserva, sumando sus costos al total.
* **RF-007 - Aplicación de Promociones:** El sistema evaluará automáticamente las condiciones de promociones vigentes (noches mínimas, fechas especiales o tipo de habitación) y aplicará los descuentos correspondientes.
* **RF-008 - Consulta de Historial de Reservas:** Tanto el cliente (sus reservas propias) como el administrador (todas las reservas del hotel) podrán consultar el historial y los detalles completos de cada reserva.

### Módulo de Pagos y Tarjeta Virtual
* **RF-009 - Gestión de Tarjeta Virtual:** Cada cliente dispondrá de una tarjeta virtual asociada con identificador único, estado (activa/inactiva) y saldo disponible.
* **RF-010 - Pago Automático de Reserva:** Antes de confirmar una reserva, el sistema verificará que la tarjeta virtual esté activa y posea saldo suficiente, debitando el valor y generando el correspondiente movimiento contable.
* **RF-011 - Recarga de Tarjeta Virtual:** El cliente podrá solicitar recargas y exclusivamente el administrador podrá aprobar y ejecutar recargas de saldo, registrando cada transacción en el historial de movimientos.

### Módulo de Cancelaciones y Reembolsos
* **RF-012 - Cancelación por parte del Cliente:** El cliente podrá cancelar una reserva con cálculo automatizado del reembolso hacia su tarjeta virtual:
  * Más de 72 horas antes del check-in: 100% de devolución.
  * Entre 24 y 72 horas antes del check-in: 80% de devolución.
  * Menos de 24 horas antes del check-in: 0% de devolución.
* **RF-013 - Cancelación por parte del Hotel:** El administrador podrá cancelar una reserva por motivos operativos o de fuerza mayor, garantizando la devolución del 100% del valor a la tarjeta virtual del cliente.
* **RF-014 - Unicidad de Reembolso:** El sistema impedirá que una reserva o pago sea reembolsado más de una vez.

### Módulo del Programa de Fidelización (Puntos HotelUQ)
* **RF-015 - Acumulación de Puntos:** Por cada $100.000 COP pagados en reservas y servicios, el cliente acumulará 100 puntos, cada uno con fecha de obtención y vigencia exacta de un (1) año.
* **RF-016 - Redención de Puntos (FIFO):** El cliente podrá redimir sus puntos válidos por descuentos o servicios. El sistema priorizará automáticamente la deducción de los lotes de puntos más próximos a vencer, impidiendo usar puntos caducados o reutilizar puntos ya canjeados.

### Módulo de Estadísticas y Reportes
* **RF-017 - Visualización de Estadísticas Interactivas:** El administrador dispondrá de gráficos visuales en JavaFX (gráfico de barras para ingresos por periodo, gráfico de pastel para porcentaje de ocupación/distribución por tipo de habitación y gráfico de líneas para evolución temporal).
* **RF-018 - Exportación de Reportes Operativos:** El sistema permitirá generar reportes en formato **TXT** y **PDF** con título, fecha de emisión, rango consultado, registros detallados y totales consolidados.

---

## 3. Reglas de Negocio

1. **RN-01 (No superposición):** Una habitación no puede tener más de una reserva activa en periodos que se crucen o solapen.
2. **RN-02 (Validez temporal):** Fecha de salida obligatoriamente mayor a la fecha de entrada.
3. **RN-03 (Solvencia previa):** No se confirma reserva si la tarjeta virtual no cubre el valor neto resultante tras descuentos y redenciones.
4. **RN-04 (Vigencia de puntos):** Los puntos acumulados caducan exactamente a los 365 días calendario desde su fecha de acreditación.
5. **RN-05 (Consumo FIFO de puntos):** Al redimir, se descuentan primero los puntos con fecha de vencimiento más cercana.
6. **RN-06 (Reembolso escalonado):**
   * $\Delta t > 72\text{ horas} \implies 100\%$ reembolso.
   * $24\text{ horas} \le \Delta t \le 72\text{ horas} \implies 80\%$ reembolso.
   * $\Delta t < 24\text{ horas} \implies 0\%$ reembolso.
   * Cancelación por Hotel $\implies 100\%$ reembolso incondicional.
7. **RN-07 (Regla Adicional 1 - Descuento por Larga Estadía):** Las reservas con una duración mayor a 7 noches reciben automáticamente un 10% de descuento adicional en la tarifa base de hospedaje.
8. **RN-08 (Regla Adicional 2 - Control de Capacidad y Check-In Límite):** Cada tipo de habitación tiene un límite máximo e inquebrantable de huéspedes (Estándar: 2, Familiar: 5, Suite: 4, Presidencial: 6). Adicionalmente, el check-in debe registrarse antes de las 18:00 hrs del día de llegada; de lo contrario, la reserva pasa a estado No-Show para reasignación preventiva.

---

## 4. Aplicación de Principios SOLID

| Principio | Aplicación en el Proyecto HotelUQ |
| :--- | :--- |
| **SRP** *(Single Responsibility)* | Separación estricta entre la entidad `Reserva` (datos del hospedaje), `CalculadoraTarifa` (cálculo de costos y descuentos), `ProcesadorPagos` (transacciones con tarjeta) y `ServicioPuntos` (acumulación y vencimiento). Cada clase tiene una única razón de cambio. |
| **OCP** *(Open/Closed)* | Nuevos tipos de promociones o políticas de reembolso se agregan implementando `PromocionStrategy` o `PoliticaCancelacionStrategy`, sin modificar el código de cálculo existente. |
| **LSP** *(Liskov Substitution)* | Las subclases `HabitacionEstandar`, `HabitacionSuite`, `HabitacionFamiliar` extienden de `Habitacion` y pueden ser utilizadas indistintamente en el motor de reservas y disponibilidad sin romper el comportamiento del sistema. |
| **ISP** *(Interface Segregation)* | Interfaces específicas y compactas: `IExportadorReporte`, `IReservaObserver`, `IComponenteServicio`, evitando interfaces monolíticas con métodos innecesarios. |
| **DIP** *(Dependency Inversion)* | Los controladores y servicios dependen de abstracciones (`IExportadorReporte`, `PoliticaCancelacionStrategy`, repositorios) y no de implementaciones concretas. |

---

## 5. Patrones de Diseño Implementados

### 5.1 Patrones Creacionales (3)
1. **Factory Method (`HabitacionFactory`):**
   * *Problema:* Creación dinámica de diferentes tipos de habitaciones con configuraciones específicas de comodidades, capacidad y tarifas.
   * *Solución:* Método factoría `crearHabitacion(TipoHabitacion, numero, piso, precioBase)` que instancia la clase correspondiente.
2. **Builder (`ReservaBuilder`):**
   * *Problema:* Una reserva requiere múltiples parámetros opcionales y secuenciales: huésped, habitación, fechas, lista de servicios simples, paquetes compuestos, promociones y puntos a redimir.
   * *Solución:* Construcción fluida paso a paso validando coherencia antes de instanciar el objeto inmutable `Reserva`.
3. **Singleton (`HotelUQ` / `SesionManager`):**
   * *Problema:* Se requiere un punto central de gestión para el estado global de la aplicación (clientes, habitaciones, reservas) y el control del usuario actualmente autenticado.
   * *Solución:* Instancia única protegida con acceso global controlado.

### 5.2 Patrones Estructurales (4)
1. **Composite (`ComponenteServicio`, `ServicioSimple`, `PaqueteServiciosComposite`):**
   * *Problema:* El hotel ofrece servicios individuales (ej. Desayuno: $25.000, Spa: $80.000) y también paquetes temáticos ("Paquete Relajación Total" o "Paquete Familiar") que agrupan múltiples servicios con un descuento integral.
   * *Solución:* Jerarquía de composición donde clientes y reservas tratan a un servicio simple y a un paquete complejo de manera polimórfica uniforme (`getPrecio()`, `getDescripcion()`).
2. **Decorator (`DecoradorReserva`, `SeguroCancelacionFlexibleDecorator`, `ServicioVIPDecorator`):**
   * *Problema:* Añadir beneficios adicionales dinámicos a una reserva sin alterar la clase base `Reserva`.
   * *Solución:* Clases decoradoras que envuelven la reserva para añadir garantías especiales (por ejemplo, cobertura contra penalidad de cancelación).
3. **Adapter (`IExportadorReporte`, `ReporteTxtAdapter`, `ReportePdfAdapter`):**
   * *Problema:* Unificar la generación de reportes en diferentes tecnologías y librerías externas (texto plano y OpenPDF) bajo una misma interfaz de alto nivel.
   * *Solución:* Adaptadores que traducen el modelo de datos de reportes del hotel a las APIs concretas de generación de archivos.
4. **Facade (`HotelFachada`):**
   * *Problema:* Confirmar una reserva involucra validar disponibilidad de habitación, calcular tarifa, aplicar promociones, validar y debitar tarjeta virtual, acreditar puntos y emitir notificaciones.
   * *Solución:* La fachada proporciona un método de alto nivel `confirmarReserva(...)` que orquesta todos los subsistemas ocultando la complejidad a los controladores JavaFX.

### 5.3 Patrones de Comportamiento (3)
1. **Strategy (`PoliticaCancelacionStrategy`, `PromocionStrategy`):**
   * *Problema:* Variabilidad en las reglas de reembolso según el tiempo de anticipación y las condiciones cambiantes de las promociones.
   * *Solución:* Encapsular cada política de cálculo en una familia de algoritmos intercambiables en tiempo de ejecución.
2. **State (`EstadoReserva`: `PendienteState`, `ConfirmadaState`, `CanceladaState`, `CompletadaState`):**
   * *Problema:* El comportamiento de una reserva cambia radicalmente según su estado (una reserva cancelada no permite check-in ni acumulación adicional, una completada no permite cancelación).
   * *Solución:* Delegar las acciones de transición y validación a objetos de estado dedicados.
3. **Observer (`IReservaObserver`, `NotificadorReserva`, `ClienteNotificador`):**
   * *Problema:* Cuando el estado de una reserva cambia o se realiza una recarga de saldo/acreditación de puntos, múltiples componentes deben ser informados automáticamente.
   * *Solución:* Sujetos que notifican a los observadores suscritos ante eventos del ciclo de vida del hotel.
4. *(Bonus)* **Template Method (`PlantillaReporte`):**
   * *Problema:* Todos los reportes operativos del hotel comparten una misma estructura (encabezado institucional, periodo, registros tabulados y cálculo de totales).
   * *Solución:* Clase base abstracta que define el algoritmo esqueleto, dejando los detalles de extracción de datos a subclases concretas.
