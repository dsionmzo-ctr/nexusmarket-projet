# Value Objects y catálogos de dominio

## Introducción

Los **Value Objects** de NexusMarket son objetos inmutables que se comparan por valor. Hay dos familias:

1. **Catálogos de negocio** (`DomainCatalog`): conjuntos cerrados de valores con `code`, `name` y `description`. **No se usan `enum` de Java para el negocio**: cada catálogo es una clase final con constantes y un método `fromCode(String)` que valida el código recibido y lanza `InvalidCatalogCodeException` si no existe. Así el dominio expresa el lenguaje del negocio y los adaptadores solo transportan el `code`.
2. **Objetos de valor compuestos**: `Money` y `Address` (records inmutables con invariantes).

```text
DomainCatalog (abstracta: code, name, description, equals/hashCode por code)
 ├── SystemRole        ├── UserStatus      ├── BuyerStatus
 ├── WarehouseType     ├── WarehouseStatus ├── ProductType
 ├── ProductStatus     ├── InventoryStatus ├── MovementType
 ├── OrderStatus       ├── PaymentMethod   ├── ShipmentStatus
 ├── ReturnReason      ├── ReturnStatus    ├── RefundStatus
 ├── Currency          └── OperationType
```

> Los códigos están en inglés (son identificadores técnicos que viajan en el contrato REST y se guardan en las bases de datos); los nombres de negocio aquí se muestran en español.

---

## SystemRole

Responsabilidades y permisos de una persona en el marketplace (RG-02: un único rol por usuario). Origen: Sección 5 y 12 de la especificación.

| Código | Significado |
|---|---|
| `BUYER` | Persona que adquiere los productos publicados. |
| `SELLER` | Tercero responsable de registrar y administrar sus propios productos. |
| `LOGISTIC_OPERATOR` | Encargado de la operación física de bodegas y despachos. |
| `ADMIN` | Responsable de la administración de vendedores, bodegas, usuarios internos y reembolsos. |
| `SUPERVISOR` | Perfil de solo consulta y seguimiento operativo. |

---

## UserStatus

Estado de acceso de un usuario al sistema. Origen: Dominio 1.

| Código | Significado |
|---|---|
| `ACTIVE` | El usuario accede con normalidad. |
| `INACTIVE` | El usuario existe pero no puede ejecutar operaciones. |
| `BLOCKED` | El acceso del usuario fue suspendido. |

---

## BuyerStatus

Estado comercial del comprador (independiente del estado de acceso del usuario). Origen: Dominio 2.

| Código | Significado |
|---|---|
| `ENABLED` | El comprador puede realizar compras. |
| `RESTRICTED` | El comprador no puede comprar temporalmente. |

---

## WarehouseType

Clasificación de bodegas por propietario. Origen: Dominio 4.

| Código | Significado |
|---|---|
| `MARKETPLACE` | Bodega operada por el marketplace. |
| `SELLER` | Bodega que pertenece a un único vendedor. |

---

## WarehouseStatus

Estado operativo de una bodega. Origen: Dominio 4.

| Código | Significado |
|---|---|
| `ACTIVE` | La bodega opera. |
| `INACTIVE` | La bodega no opera: no recibe ingresos ni atiende reservas. |

---

## ProductType

Tipo de producto ofrecido. Origen: Dominio 5.

| Código | Significado |
|---|---|
| `PHYSICAL` | Bien que requiere inventario y despacho. |
| `DIGITAL` | Bien de entrega inmediata tras el pago. |

---

## ProductStatus

Ciclo de visibilidad de un producto del catálogo. Origen: Dominio 5.

| Código | Significado |
|---|---|
| `PUBLISHED` | Visible en el catálogo público. |
| `SUSPENDED` | No visible temporalmente (también es el estado inicial). |
| `DISCONTINUED` | Retirado de forma permanente (terminal). |

---

## InventoryStatus

Estado de calidad de un registro de inventario. Origen: Dominio 6.

| Código | Significado |
|---|---|
| `AVAILABLE` | El stock puede reservarse. |
| `DAMAGED` | El stock no puede reservarse (validación crítica de inventario). |

---

## MovementType

Clase de movimiento de inventario. Origen: Dominio 6 (movimientos).

| Código | Significado |
|---|---|
| `INFLOW` | Ingreso de stock a una bodega. |
| `RESERVATION` | Stock reservado por un pedido. |
| `RESERVATION_RELEASE` | Reserva devuelta (pedido cancelado). |
| `SALE` | Salida por venta: el stock reservado sale de la bodega tras el pago. |
| `ADJUSTMENT` | Corrección manual del stock físico. |
| `RETURN` | Stock que regresa por una devolución de comprador. |

---

## OrderStatus

Ciclo de vida del pedido. Origen: Dominio 7.

| Código | Significado |
|---|---|
| `PENDING_PAYMENT` | A la espera de confirmación financiera. |
| `PAID` | Pago confirmado; inicia el alistamiento. |
| `SHIPPED` | El pedido salió físicamente de la bodega. |
| `DELIVERED` | Entrega concluida; el pedido queda finalizado. |
| `CANCELLED` | Pedido cancelado antes del pago; queda finalizado. |

---

## PaymentMethod

Medio de pago aceptado. Origen: Facturación / pedidos.

| Código | Significado |
|---|---|
| `CREDIT_CARD` | Tarjeta de crédito. |
| `DEBIT_CARD` | Tarjeta débito. |
| `BANK_TRANSFER` | Transferencia bancaria. |
| `DIGITAL_WALLET` | Billetera digital. |

---

## ShipmentStatus

Ciclo de vida de un envío. Origen: Gestión de envíos.

| Código | Significado |
|---|---|
| `PREPARING` | Pedido en empaque. |
| `SHIPPED` | Envío en camino. |
| `DELIVERED` | Envío entregado al comprador. |

---

## ReturnReason

Motivo declarado por el comprador para una devolución. Origen: Devoluciones.

| Código | Significado |
|---|---|
| `DEFECTIVE` | El producto llegó defectuoso. |
| `WRONG_ITEM` | Producto distinto al comprado. |
| `NOT_AS_DESCRIBED` | El producto no corresponde a su descripción. |
| `CHANGED_MIND` | El comprador se arrepintió. |

---

## ReturnStatus

Ciclo de vida de una devolución. Origen: Devoluciones.

| Código | Significado |
|---|---|
| `REQUESTED` | Devolución solicitada por el comprador. |
| `RECEIVED` | Mercancía devuelta recibida en una bodega. |
| `REJECTED` | Devolución rechazada. |

---

## RefundStatus

Ciclo de vida de un reembolso. Origen: Reembolsos.

| Código | Significado |
|---|---|
| `REQUESTED` | Reembolso abierto junto con la devolución. |
| `APPROVED` | Aprobado por el administrador. |
| `REJECTED` | Rechazado por el administrador. |

---

## Currency

Moneda soportada por el marketplace. Origen: Facturación.

| Código | Significado |
|---|---|
| `COP` | Peso colombiano. |
| `USD` | Dólar estadounidense. |
| `EUR` | Euro. |

---

## OperationType

Operación de negocio significativa ejecutada sobre un recurso del marketplace. Origen: Trazabilidad.

| Código | Significado |
|---|---|
| `BUYER_REGISTRATION` | Registro de un comprador. |
| `SELLER_REGISTRATION` | Incorporación de un vendedor por el administrador. |
| `USER_REGISTRATION` | Creación de un usuario del sistema. |
| `USER_STATUS_CHANGE` | Cambio del estado de acceso de un usuario. |
| `BUYER_STATUS_CHANGE` | Cambio del estado comercial de un comprador. |
| `BUYER_ADDRESS_ADDITION` | Alta de una dirección de entrega. |
| `BUYER_ADDRESS_REMOVAL` | Baja de una dirección de entrega. |
| `WAREHOUSE_CREATION` | Creación de una bodega. |
| `WAREHOUSE_UPDATE` | Actualización de una bodega. |
| `WAREHOUSE_STATUS_CHANGE` | Cambio de estado de una bodega. |
| `PRODUCT_REGISTRATION` | Registro de un producto. |
| `PRODUCT_UPDATE` | Actualización de un producto. |
| `PRODUCT_VARIANT_ADDITION` | Alta de una variante de producto. |
| `PRODUCT_PUBLICATION` | Publicación de un producto. |
| `PRODUCT_SUSPENSION` | Suspensión de un producto. |
| `PRODUCT_DISCONTINUATION` | Descontinuación de un producto. |
| `INVENTORY_INFLOW` | Ingreso de stock a una bodega. |
| `INVENTORY_ADJUSTMENT` | Ajuste manual del stock físico. |
| `INVENTORY_DAMAGE_MARKING` | Inventario marcado como dañado. |
| `INVENTORY_AVAILABILITY_RESTORATION` | Inventario marcado nuevamente como disponible. |
| `STOCK_RESERVATION` | Stock reservado por un pedido. |
| `STOCK_RELEASE` | Stock reservado liberado. |
| `STOCK_SALE` | Stock reservado confirmado como vendido. |
| `ORDER_CREATION` | Creación de un pedido a partir del carrito. |
| `ORDER_PAYMENT` | Pago de un pedido. |
| `ORDER_CANCELLATION` | Cancelación de un pedido. |
| `ORDER_DELIVERY` | Entrega de un pedido. |
| `INVOICE_ISSUANCE` | Emisión de una factura. |
| `SHIPMENT_CREATION` | Apertura de un envío. |
| `SHIPMENT_DISPATCH` | Despacho de un envío. |
| `SHIPMENT_DELIVERY` | Confirmación de entrega de un envío. |
| `RETURN_REQUEST` | Solicitud de una devolución. |
| `RETURN_RECEIPT` | Recepción de mercancía devuelta. |
| `RETURN_REJECTION` | Rechazo de una devolución. |
| `REFUND_APPROVAL` | Aprobación de un reembolso. |
| `REFUND_REJECTION` | Rechazo de un reembolso. |

---

## Money

Cantidad de dinero en una moneda concreta.

```java
public record Money(BigDecimal amount, Currency currency)
```

- **Invariantes:** `amount` y `currency` son obligatorios y el monto nunca es negativo.
- Los montos se normalizan a **2 decimales** (`HALF_UP`).
- Operaciones: `of(...)`, `zero(currency)`, `isPositive`, `add`, `multiply(int)`, `percentage(BigDecimal)`, `hasSameCurrency`. Sumar monedas distintas es un error de dominio.
- Se compara por valor (`BigDecimal` normalizado) y es inmutable.

---

## Address

Ubicación de entrega o de almacenamiento.

```java
public record Address(String street, String city, String country)
```

- Las tres partes son obligatorias y no pueden estar vacías.
- Dos direcciones con las mismas partes son la misma dirección (así se detecta una dirección repetida y se localiza la que se va a eliminar).
- Se usa en `Buyer` (dirección principal y direcciones adicionales), `Warehouse` y `Order` (dirección de envío, que debe pertenecer al comprador).

---

## Enumeraciones técnicas (`application.domain.enums`)

Además de los catálogos de negocio existen tres `enum` de Java para conceptos **técnicos** (no del negocio):

| Enum | Valores | Uso |
|---|---|---|
| `AuditSeverity` | `INFORMATION`, `WARNING`, `ERROR`, `CRITICAL` | Severidad de un `AuditLog` |
| `NotificationChannel` | `EMAIL`, `SMS`, `PUSH_NOTIFICATION` | Canal de una `Notification` |
| `ApprovalDecision` | `APPROVED`, `REJECTED` | Resultado de un proceso de aprobación |
