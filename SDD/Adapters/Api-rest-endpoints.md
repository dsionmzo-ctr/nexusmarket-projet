# Endpoints REST de la API

## Introducción

Este documento lista **los 81 endpoints** de NexusMarket, agrupados por controlador. Todos viven bajo `/api/v1`, usan JSON (`application/json`), devuelven el sobre de error estándar descrito en `Global-exception-handler.md` y —salvo los marcados como *Público*— exigen `Authorization: Bearer <token>`.

## Convenciones

| Concepto | Regla |
|---|---|
| Seguridad | `SecurityConfig` autoriza por **prefijo de ruta y rol**: `/buyer/**`→BUYER, `/seller/**`→SELLER, `/logistic-operator/**`→LOGISTIC_OPERATOR, `/admin/**`→ADMIN, `/supervisor/**`→SUPERVISOR |
| Sin token | **401** `AUTHENTICATION_REQUIRED` |
| Rol equivocado | **403** `FORBIDDEN` |
| Éxito | `200` consultas y cambios, `201` altas, `204` sin cuerpo |
| Ids de ruta | `@Positive Long`; un id no numérico o ≤ 0 devuelve **400** |
| Controladores | Solo traducen HTTP ↔ puertos de entrada con mappers estáticos; no contienen lógica de negocio |
| Principal | `@AuthenticationPrincipal AuthenticatedUserPrincipal` → `principal.domainUser()` |

---

## Autenticación y registro público (`AuthRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| POST | `/api/v1/auth/login` | Público | `LoginRequestDTO` | `LoginResponseDTO` | 200 | `publicAccessPort.login` | `LoginService` |
| POST | `/api/v1/auth/logout` | Autenticado | — | `void` | 204 | `publicAccessPort.logout` | `LogoutService` |
| POST | `/api/v1/auth/register/buyer` | Público | `RegisterBuyerRequestDTO` | `BuyerResponseDTO` | 201 | `publicAccessPort.registerBuyer` | `RegisterBuyerService` |
| POST | `/api/v1/auth/register/user` | Público | `RegisterUserRequestDTO` | `UserResponseDTO` | 201 | `publicAccessPort.registerBuyerUser` | `RegisterBuyerUserService` |

### Descripción de cada endpoint

- **POST `/api/v1/auth/login`** — Autentica a un usuario del sistema por correo y contraseña y emite el token de acceso. Un correo desconocido y una contraseña errónea producen el mismo error. Flujo: buscar el usuario, validar la contraseña, validar el estado del usuario y generar el token mediante los puertos de salida.
- **POST `/api/v1/auth/logout`** — Cierra la sesión de un usuario: se incrementa su authTokenVersion, de modo que todo token emitido antes es rechazado desde ese momento.
- **POST `/api/v1/auth/register/buyer`** — Autorregistro de un comprador: valida los datos obligatorios (identificación, nombre, correo, dirección principal) y la unicidad de identificación y correo en la plataforma. Es una operación anónima, por eso la traza Operation/Audit se registra al crear la cuenta de usuario.
- **POST `/api/v1/auth/register/user`** — Crea el usuario del sistema (credenciales) de un comprador ya registrado. La identidad y el rol se toman del comprador; el correo y la identificación deben ser únicos en la plataforma.

---

## Catálogo público (`PublicCatalogRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| GET | `/api/v1/catalog/products` | Público | — | `List<ProductResponseDTO>` | 200 | `publicAccessPort.consultPublishedProducts` | `ConsultPublishedProductsService` |
| GET | `/api/v1/catalog/products/{productId}` | Público | — | `ProductResponseDTO` | 200 | `publicAccessPort.consultPublishedProduct` | `ConsultPublishedProductService` |

### Descripción de cada endpoint

- **GET `/api/v1/catalog/products`** — Catálogo público: consulta los productos PUBLICADOS (sin autenticación).
- **GET `/api/v1/catalog/products/{productId}`** — Catálogo público: consulta un producto PUBLICADO. Un producto no publicado se informa como no encontrado.

---

## Comprador (`BuyerRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| GET | `/api/v1/buyer/profile` | BUYER | — | `BuyerResponseDTO` | 200 | `buyerPort.consultMyProfile` | `ConsultBuyerProfileService` |
| POST | `/api/v1/buyer/addresses` | BUYER | `AddressRequestDTO` | `BuyerResponseDTO` | 201 | `buyerPort.addAddress` | `AddBuyerAddressService` |
| DELETE | `/api/v1/buyer/addresses` | BUYER | `AddressRequestDTO` | `BuyerResponseDTO` | 200 | `buyerPort.removeAddress` | `RemoveBuyerAddressService` |
| GET | `/api/v1/buyer/cart` | BUYER | — | `CartResponseDTO` | 200 | `buyerPort.consultMyCart` | `ConsultCartService` |
| POST | `/api/v1/buyer/cart/items` | BUYER | `CartItemRequestDTO` | `CartResponseDTO` | 201 | `buyerPort.addItemToCart` | `AddItemToCartService` |
| PUT | `/api/v1/buyer/cart/items/{variantId}` | BUYER | `UpdateCartItemRequestDTO` | `CartResponseDTO` | 200 | `buyerPort.updateCartItem` | `UpdateCartItemQuantityService` |
| DELETE | `/api/v1/buyer/cart/items/{variantId}` | BUYER | — | `CartResponseDTO` | 200 | `buyerPort.removeCartItem` | `RemoveCartItemService` |
| DELETE | `/api/v1/buyer/cart` | BUYER | — | `CartResponseDTO` | 200 | `buyerPort.clearCart` | `ClearCartService` |
| POST | `/api/v1/buyer/orders` | BUYER | — | `OrderResponseDTO` | 201 | `buyerPort.checkout` | `CheckoutService` |
| PATCH | `/api/v1/buyer/orders/{orderId}/payment` | BUYER | `PayOrderRequestDTO` | `OrderResponseDTO` | 200 | `buyerPort.payOrder` | `PayOrderService` |
| PATCH | `/api/v1/buyer/orders/{orderId}/cancel` | BUYER | — | `OrderResponseDTO` | 200 | `buyerPort.cancelOrder` | `CancelOrderService` |
| GET | `/api/v1/buyer/orders` | BUYER | — | `List<OrderResponseDTO>` | 200 | `buyerPort.consultMyOrders` | `ConsultBuyerOrdersService` |
| GET | `/api/v1/buyer/orders/{orderId}` | BUYER | — | `OrderResponseDTO` | 200 | `buyerPort.consultMyOrder` | `FindBuyerOrderService` |
| GET | `/api/v1/buyer/orders/{orderId}/invoice` | BUYER | — | `InvoiceResponseDTO` | 200 | `buyerPort.consultMyInvoice` | `ConsultOrderInvoiceService` |
| GET | `/api/v1/buyer/orders/{orderId}/shipment` | BUYER | — | `ShipmentResponseDTO` | 200 | `buyerPort.consultMyShipment` | `ConsultOrderShipmentService` |
| POST | `/api/v1/buyer/returns` | BUYER | `RequestReturnRequestDTO` | `ReturnRequestResponseDTO` | 201 | `buyerPort.requestReturn` | `RequestReturnService` |
| GET | `/api/v1/buyer/returns` | BUYER | — | `List<ProductReturnResponseDTO>` | 200 | `buyerPort.consultMyReturns` | `ConsultBuyerReturnsService` |
| GET | `/api/v1/buyer/refunds` | BUYER | — | `List<RefundResponseDTO>` | 200 | `buyerPort.consultMyRefunds` | `ConsultBuyerRefundsService` |
| GET | `/api/v1/buyer/operations` | BUYER | — | `List<OperationResponseDTO>` | 200 | `buyerPort.consultMyOperations` | `ConsultUserOperationsService` |

### Descripción de cada endpoint

- **GET `/api/v1/buyer/profile`** — Consulta el perfil del comprador autenticado (un comprador nunca lee a otros compradores).
- **POST `/api/v1/buyer/addresses`** — Agrega una dirección de entrega adicional al perfil del comprador autenticado.
- **DELETE `/api/v1/buyer/addresses`** — Elimina una dirección adicional del perfil del comprador autenticado (la dirección principal no se puede eliminar).
- **GET `/api/v1/buyer/cart`** — Consulta el carrito del comprador autenticado.
- **POST `/api/v1/buyer/cart/items`** — Agrega una variante de un producto PUBLICADO al carrito del comprador autenticado; si la variante ya está en el carrito, las cantidades se suman.
- **PUT `/api/v1/buyer/cart/items/{variantId}`** — Cambia la cantidad de una variante que ya está en el carrito del comprador autenticado.
- **DELETE `/api/v1/buyer/cart/items/{variantId}`** — Quita una variante del carrito del comprador autenticado.
- **DELETE `/api/v1/buyer/cart`** — Vacía el carrito del comprador autenticado.
- **POST `/api/v1/buyer/orders`** — Confirma el carrito del comprador autenticado: valida cada línea contra el catálogo, reserva el stock de los productos físicos, crea el pedido en PENDIENTE DE PAGO y vacía el carrito.
- **PATCH `/api/v1/buyer/orders/{orderId}/payment`** — Registra el pago de un pedido del comprador autenticado e inicia el flujo posterior: el stock reservado pasa a SALIDA POR VENTA, se emite la factura y, para productos físicos, se abre el envío. Los pedidos compuestos solo por productos digitales se entregan de inmediato.
- **PATCH `/api/v1/buyer/orders/{orderId}/cancel`** — Cancela un pedido sin pagar del comprador autenticado y libera su stock reservado.
- **GET `/api/v1/buyer/orders`** — Consulta los pedidos del comprador autenticado.
- **GET `/api/v1/buyer/orders/{orderId}`** — Carga un pedido y verifica que pertenezca al comprador autenticado.
- **GET `/api/v1/buyer/orders/{orderId}/invoice`** — Consulta la factura de un pedido del comprador autenticado.
- **GET `/api/v1/buyer/orders/{orderId}/shipment`** — Consulta el envío de un pedido del comprador autenticado.
- **POST `/api/v1/buyer/returns`** — El comprador autenticado solicita la devolución de un producto físico entregado y, con ella, el reembolso de su precio. El pedido entregado nunca se modifica (un pedido finalizado es inmutable).
- **GET `/api/v1/buyer/returns`** — Consulta las devoluciones del comprador autenticado.
- **GET `/api/v1/buyer/refunds`** — Consulta los reembolsos del comprador autenticado.
- **GET `/api/v1/buyer/operations`** — Consulta las operaciones ejecutadas por el usuario autenticado (cualquier rol: «mis operaciones»).

---

## Vendedor (`SellerRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| POST | `/api/v1/seller/products` | SELLER | `RegisterProductRequestDTO` | `ProductResponseDTO` | 201 | `sellerPort.registerProduct` | `RegisterProductService` |
| PUT | `/api/v1/seller/products/{productId}` | SELLER | `UpdateProductRequestDTO` | `ProductResponseDTO` | 200 | `sellerPort.updateProduct` | `UpdateProductService` |
| POST | `/api/v1/seller/products/{productId}/variants` | SELLER | `ProductVariantRequestDTO` | `ProductResponseDTO` | 201 | `sellerPort.addProductVariant` | `AddProductVariantService` |
| PATCH | `/api/v1/seller/products/{productId}/publish` | SELLER | — | `ProductResponseDTO` | 200 | `sellerPort.publishProduct` | `PublishProductService` |
| PATCH | `/api/v1/seller/products/{productId}/suspend` | SELLER | — | `ProductResponseDTO` | 200 | `sellerPort.suspendProduct` | `SuspendProductService` |
| PATCH | `/api/v1/seller/products/{productId}/discontinue` | SELLER | — | `ProductResponseDTO` | 200 | `sellerPort.discontinueProduct` | `DiscontinueProductService` |
| GET | `/api/v1/seller/products` | SELLER | — | `List<ProductResponseDTO>` | 200 | `sellerPort.consultMyProducts` | `ConsultSellerProductsService` |
| GET | `/api/v1/seller/warehouses` | SELLER | — | `List<WarehouseResponseDTO>` | 200 | `sellerPort.consultMyWarehouses` | `ConsultSellerWarehousesService` |
| POST | `/api/v1/seller/inventory/inflows` | SELLER | `InventoryInflowRequestDTO` | `InventoryResponseDTO` | 201 | `sellerPort.registerInventoryInflow` | `RegisterInventoryInflowService` |
| GET | `/api/v1/seller/inventory` | SELLER | — | `List<InventoryResponseDTO>` | 200 | `sellerPort.consultMyInventory` | `ConsultSellerInventoryService` |
| GET | `/api/v1/seller/orders` | SELLER | — | `List<OrderResponseDTO>` | 200 | `sellerPort.consultMyOrders` | `ConsultSellerOrdersService` |
| GET | `/api/v1/seller/orders/{orderId}` | SELLER | — | `OrderResponseDTO` | 200 | `sellerPort.consultMyOrder` | `ConsultSellerOrderService` |
| GET | `/api/v1/seller/operations` | SELLER | — | `List<OperationResponseDTO>` | 200 | `sellerPort.consultMyOperations` | `ConsultUserOperationsService` |

### Descripción de cada endpoint

- **POST `/api/v1/seller/products`** — Un VENDEDOR registra uno de sus productos con sus variantes. El producto se crea SUSPENDIDO (no visible) y el SKU de cada variante debe ser único en el catálogo.
- **PUT `/api/v1/seller/products/{productId}`** — Un VENDEDOR actualiza el nombre, la descripción y el precio de uno de sus productos.
- **POST `/api/v1/seller/products/{productId}/variants`** — Un VENDEDOR agrega una variante (color, talla, modelo…) a uno de sus productos. El SKU debe ser único.
- **PATCH `/api/v1/seller/products/{productId}/publish`** — Un VENDEDOR publica (SUSPENDIDO → PUBLICADO) uno de sus productos.
- **PATCH `/api/v1/seller/products/{productId}/suspend`** — Un VENDEDOR suspende (PUBLICADO → SUSPENDIDO) uno de sus productos.
- **PATCH `/api/v1/seller/products/{productId}/discontinue`** — Un VENDEDOR descontinúa de forma permanente uno de sus productos.
- **GET `/api/v1/seller/products`** — Consulta los productos del vendedor autenticado en cualquier estado.
- **GET `/api/v1/seller/warehouses`** — Consulta las bodegas del vendedor autenticado.
- **POST `/api/v1/seller/inventory/inflows`** — Registra stock que entra a una bodega (INGRESO) para la variante de un producto físico. Un VENDEDOR solo puede hacerlo sobre sus productos en sus bodegas; un OPERADOR LOGÍSTICO en cualquier bodega. El registro de inventario se crea en el primer ingreso.
- **GET `/api/v1/seller/inventory`** — Consulta el inventario de los productos del vendedor autenticado.
- **GET `/api/v1/seller/orders`** — Consulta los pedidos que contienen productos del vendedor autenticado, restringidos a las líneas del vendedor.
- **GET `/api/v1/seller/orders/{orderId}`** — Consulta un pedido que contiene productos del vendedor autenticado, restringido a las líneas del vendedor.
- **GET `/api/v1/seller/operations`** — Consulta las operaciones ejecutadas por el usuario autenticado (cualquier rol: «mis operaciones»).

---

## Operador logístico (`LogisticOperatorRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| GET | `/api/v1/logistic-operator/warehouses` | LOGISTIC_OPERATOR | — | `List<WarehouseResponseDTO>` | 200 | `logisticOperatorPort.consultWarehouses` | `ConsultWarehousesService` |
| GET | `/api/v1/logistic-operator/warehouses/{warehouseId}/inventory` | LOGISTIC_OPERATOR | — | `List<InventoryResponseDTO>` | 200 | `logisticOperatorPort.consultWarehouseInventory` | `ConsultWarehouseInventoryService` |
| GET | `/api/v1/logistic-operator/inventory/{inventoryId}/movements` | LOGISTIC_OPERATOR | — | `List<InventoryMovementResponseDTO>` | 200 | `logisticOperatorPort.consultInventoryMovements` | `ConsultInventoryMovementsService` |
| POST | `/api/v1/logistic-operator/inventory/inflows` | LOGISTIC_OPERATOR | `InventoryInflowRequestDTO` | `InventoryResponseDTO` | 201 | `logisticOperatorPort.registerInventoryInflow` | `RegisterInventoryInflowService` |
| PATCH | `/api/v1/logistic-operator/inventory/{inventoryId}/adjustment` | LOGISTIC_OPERATOR | `InventoryAdjustmentRequestDTO` | `InventoryResponseDTO` | 200 | `logisticOperatorPort.adjustInventory` | `AdjustInventoryService` |
| PATCH | `/api/v1/logistic-operator/inventory/{inventoryId}/damaged` | LOGISTIC_OPERATOR | — | `InventoryResponseDTO` | 200 | `logisticOperatorPort.markInventoryDamaged` | `MarkInventoryDamagedService` |
| PATCH | `/api/v1/logistic-operator/inventory/{inventoryId}/available` | LOGISTIC_OPERATOR | — | `InventoryResponseDTO` | 200 | `logisticOperatorPort.markInventoryAvailable` | `MarkInventoryAvailableService` |
| GET | `/api/v1/logistic-operator/orders` | LOGISTIC_OPERATOR | — | `List<OrderResponseDTO>` | 200 | `logisticOperatorPort.consultOrders` | `ConsultAllOrdersService` |
| GET | `/api/v1/logistic-operator/orders/{orderId}` | LOGISTIC_OPERATOR | — | `OrderResponseDTO` | 200 | `logisticOperatorPort.consultOrder` | `ConsultOrderService` |
| GET | `/api/v1/logistic-operator/shipments` | LOGISTIC_OPERATOR | — | `List<ShipmentResponseDTO>` | 200 | `logisticOperatorPort.consultShipments` | `ConsultShipmentsService` |
| PATCH | `/api/v1/logistic-operator/shipments/{shipmentId}/dispatch` | LOGISTIC_OPERATOR | — | `ShipmentResponseDTO` | 200 | `logisticOperatorPort.dispatchShipment` | `DispatchShipmentService` |
| PATCH | `/api/v1/logistic-operator/shipments/{shipmentId}/delivery` | LOGISTIC_OPERATOR | — | `ShipmentResponseDTO` | 200 | `logisticOperatorPort.confirmDelivery` | `ConfirmShipmentDeliveryService` |
| GET | `/api/v1/logistic-operator/returns` | LOGISTIC_OPERATOR | — | `List<ProductReturnResponseDTO>` | 200 | `logisticOperatorPort.consultReturnsToReceive` | `ConsultReturnsToReceiveService` |
| PATCH | `/api/v1/logistic-operator/returns/{returnId}/reception` | LOGISTIC_OPERATOR | `ReceiveReturnRequestDTO` | `ProductReturnResponseDTO` | 200 | `logisticOperatorPort.receiveReturn` | `ReceiveReturnService` |
| PATCH | `/api/v1/logistic-operator/returns/{returnId}/rejection` | LOGISTIC_OPERATOR | — | `ProductReturnResponseDTO` | 200 | `logisticOperatorPort.rejectReturn` | `RejectReturnService` |
| GET | `/api/v1/logistic-operator/operations` | LOGISTIC_OPERATOR | — | `List<OperationResponseDTO>` | 200 | `logisticOperatorPort.consultMyOperations` | `ConsultUserOperationsService` |

Parámetros de consulta: `GET /api/v1/logistic-operator/shipments` → `status`

### Descripción de cada endpoint

- **GET `/api/v1/logistic-operator/warehouses`** — Consulta todas las bodegas (ADMIN, OPERADOR LOGÍSTICO y SUPERVISOR).
- **GET `/api/v1/logistic-operator/warehouses/{warehouseId}/inventory`** — Consulta el inventario almacenado en una bodega (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR; un VENDEDOR solo en sus propias bodegas).
- **GET `/api/v1/logistic-operator/inventory/{inventoryId}/movements`** — Consulta el libro de movimientos de un registro de inventario (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **POST `/api/v1/logistic-operator/inventory/inflows`** — Registra stock que entra a una bodega (INGRESO) para la variante de un producto físico. Un VENDEDOR solo puede hacerlo sobre sus productos en sus bodegas; un OPERADOR LOGÍSTICO en cualquier bodega. El registro de inventario se crea en el primer ingreso.
- **PATCH `/api/v1/logistic-operator/inventory/{inventoryId}/adjustment`** — Un OPERADOR LOGÍSTICO corrige el stock físico de un registro de inventario (AJUSTE). El stock nunca puede quedar negativo ni inferior a lo reservado.
- **PATCH `/api/v1/logistic-operator/inventory/{inventoryId}/damaged`** — Un OPERADOR LOGÍSTICO marca un registro de inventario como DAÑADO: deja de poder reservarse.
- **PATCH `/api/v1/logistic-operator/inventory/{inventoryId}/available`** — Un OPERADOR LOGÍSTICO vuelve a marcar como DISPONIBLE un registro de inventario dañado.
- **GET `/api/v1/logistic-operator/orders`** — Consulta todos los pedidos del marketplace (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **GET `/api/v1/logistic-operator/orders/{orderId}`** — Consulta un pedido del marketplace (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **GET `/api/v1/logistic-operator/shipments`** — Consulta los envíos, opcionalmente filtrados por estado (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **PATCH `/api/v1/logistic-operator/shipments/{shipmentId}/dispatch`** — Un OPERADOR LOGÍSTICO despacha un envío (salida física de la bodega). El pedido pasa a DESPACHADO.
- **PATCH `/api/v1/logistic-operator/shipments/{shipmentId}/delivery`** — Un OPERADOR LOGÍSTICO confirma la entrega de un envío. El pedido pasa a ENTREGADO (finalizado).
- **GET `/api/v1/logistic-operator/returns`** — Un OPERADOR LOGÍSTICO consulta las devoluciones pendientes de recibir (estado SOLICITADA).
- **PATCH `/api/v1/logistic-operator/returns/{returnId}/reception`** — Un OPERADOR LOGÍSTICO recibe en una bodega la mercancía devuelta. Las unidades reingresan al inventario con un movimiento de DEVOLUCIÓN.
- **PATCH `/api/v1/logistic-operator/returns/{returnId}/rejection`** — Un OPERADOR LOGÍSTICO rechaza una devolución (mercancía no aceptada). El reembolso abierto con ella también se rechaza.
- **GET `/api/v1/logistic-operator/operations`** — Consulta las operaciones ejecutadas por el usuario autenticado (cualquier rol: «mis operaciones»).

---

## Administrador (`AdminRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| POST | `/api/v1/admin/sellers` | ADMIN | `RegisterSellerRequestDTO` | `SellerResponseDTO` | 201 | `adminPort.registerSeller` | `IncorporateSellerService` |
| POST | `/api/v1/admin/seller-users` | ADMIN | `RegisterSellerUserRequestDTO` | `UserResponseDTO` | 201 | `adminPort.registerSellerUser` | `RegisterSellerUserService` |
| POST | `/api/v1/admin/staff-users` | ADMIN | `RegisterStaffUserRequestDTO` | `UserResponseDTO` | 201 | `adminPort.registerStaffUser` | `RegisterStaffUserService` |
| GET | `/api/v1/admin/users` | ADMIN | — | `List<UserResponseDTO>` | 200 | `adminPort.consultUsers` | `ConsultUsersService` |
| PATCH | `/api/v1/admin/users/{userId}/status` | ADMIN | `ChangeStatusRequestDTO` | `UserResponseDTO` | 200 | `adminPort.changeUserStatus` | `ChangeUserStatusService` |
| PATCH | `/api/v1/admin/buyers/{buyerId}/status` | ADMIN | `ChangeStatusRequestDTO` | `BuyerResponseDTO` | 200 | `adminPort.changeBuyerStatus` | `ChangeBuyerStatusService` |
| POST | `/api/v1/admin/warehouses` | ADMIN | `CreateWarehouseRequestDTO` | `WarehouseResponseDTO` | 201 | `adminPort.createWarehouse` | `CreateWarehouseService` |
| PUT | `/api/v1/admin/warehouses/{warehouseId}` | ADMIN | `UpdateWarehouseRequestDTO` | `WarehouseResponseDTO` | 200 | `adminPort.updateWarehouse` | `UpdateWarehouseService` |
| PATCH | `/api/v1/admin/warehouses/{warehouseId}/status` | ADMIN | `ChangeStatusRequestDTO` | `WarehouseResponseDTO` | 200 | `adminPort.changeWarehouseStatus` | `ChangeWarehouseStatusService` |
| GET | `/api/v1/admin/warehouses` | ADMIN | — | `List<WarehouseResponseDTO>` | 200 | `adminPort.consultWarehouses` | `ConsultWarehousesService` |
| GET | `/api/v1/admin/refunds/pending` | ADMIN | — | `List<RefundResponseDTO>` | 200 | `adminPort.consultPendingRefunds` | `ConsultRefundsService` |
| PATCH | `/api/v1/admin/refunds/{refundId}/approval` | ADMIN | — | `RefundResponseDTO` | 200 | `adminPort.approveRefund` | `ApproveRefundService` |
| PATCH | `/api/v1/admin/refunds/{refundId}/rejection` | ADMIN | — | `RefundResponseDTO` | 200 | `adminPort.rejectRefund` | `RejectRefundService` |
| GET | `/api/v1/admin/audit-logs` | ADMIN | — | `AuditLogPageResponseDTO` | 200 | `adminPort.consultAuditLog` | `ConsultAuditLogsService` |
| GET | `/api/v1/admin/operations` | ADMIN | — | `List<OperationResponseDTO>` | 200 | `adminPort.consultAllOperations` | `ConsultAllOperationsService` |
| GET | `/api/v1/admin/summary` | ADMIN | — | `AdministrativeSummaryResponseDTO` | 200 | `adminPort.consultSummary` | `ConsultAdministrativeSummaryService` |

Parámetros de consulta: `GET /api/v1/admin/audit-logs` → `page, size, userId, operationType`

### Descripción de cada endpoint

- **POST `/api/v1/admin/sellers`** — Coordina el flujo de incorporación del negocio: el ADMIN registra al vendedor y su primera bodega (la bodega se crea para el nuevo vendedor). Se ejecuta en una sola transacción.
- **POST `/api/v1/admin/seller-users`** — Crea el usuario del sistema (credenciales) de un vendedor ya registrado. Solo el ADMIN puede hacerlo.
- **POST `/api/v1/admin/staff-users`** — Crea un usuario OPERADOR LOGÍSTICO, SUPERVISOR o ADMIN. Solo el ADMIN puede hacerlo.
- **GET `/api/v1/admin/users`** — Consulta los usuarios de la plataforma (ADMIN y SUPERVISOR).
- **PATCH `/api/v1/admin/users/{userId}/status`** — Cambia el estado de acceso de un usuario (ACTIVO, INACTIVO, BLOQUEADO). Solo el ADMIN puede hacerlo. La versión del token se incrementa, de modo que las sesiones del usuario afectado se cierran.
- **PATCH `/api/v1/admin/buyers/{buyerId}/status`** — Cambia el estado comercial del comprador (HABILITADO, RESTRINGIDO). Solo el ADMIN puede hacerlo.
- **POST `/api/v1/admin/warehouses`** — Crea una bodega de tipo MARKETPLACE o VENDEDOR. Solo el ADMIN puede hacerlo.
- **PUT `/api/v1/admin/warehouses/{warehouseId}`** — Actualiza el nombre y la dirección de una bodega. Solo el ADMIN puede hacerlo.
- **PATCH `/api/v1/admin/warehouses/{warehouseId}/status`** — Activa o desactiva una bodega. Solo el ADMIN puede hacerlo.
- **GET `/api/v1/admin/warehouses`** — Consulta todas las bodegas (ADMIN, OPERADOR LOGÍSTICO y SUPERVISOR).
- **GET `/api/v1/admin/refunds/pending`** — Consulta los reembolsos, opcionalmente filtrados por estado (ADMIN y SUPERVISOR).
- **PATCH `/api/v1/admin/refunds/{refundId}/approval`** — El ADMIN aprueba un reembolso. La mercancía devuelta debe haberse recibido antes. La decisión registra quién decidió y cuándo.
- **PATCH `/api/v1/admin/refunds/{refundId}/rejection`** — El ADMIN rechaza un reembolso. La decisión registra quién decidió y cuándo.
- **GET `/api/v1/admin/audit-logs`** — Consulta la traza de auditoría (ADMIN y SUPERVISOR). El filtro puede traer el usuario ejecutor y/o el tipo de operación; si vienen ambos, se aplican ambas condiciones.
- **GET `/api/v1/admin/operations`** — Consulta todas las operaciones registradas por el sistema (ADMIN y SUPERVISOR).
- **GET `/api/v1/admin/summary`** — Construye las cifras consolidadas de solo lectura para la consulta administrativa (ADMIN y SUPERVISOR).

---

## Supervisor (`SupervisorRestController`)

| Método | Ruta | Rol | Cuerpo | Respuesta | HTTP | Caso de uso | Servicio de dominio |
|---|---|---|---|---|---|---|---|
| GET | `/api/v1/supervisor/summary` | SUPERVISOR | — | `AdministrativeSummaryResponseDTO` | 200 | `supervisorPort.consultSummary` | `ConsultAdministrativeSummaryService` |
| GET | `/api/v1/supervisor/users` | SUPERVISOR | — | `List<UserResponseDTO>` | 200 | `supervisorPort.consultUsers` | `ConsultUsersService` |
| GET | `/api/v1/supervisor/products` | SUPERVISOR | — | `List<ProductResponseDTO>` | 200 | `supervisorPort.consultProducts` | `ConsultAllProductsService` |
| GET | `/api/v1/supervisor/warehouses` | SUPERVISOR | — | `List<WarehouseResponseDTO>` | 200 | `supervisorPort.consultWarehouses` | `ConsultWarehousesService` |
| GET | `/api/v1/supervisor/warehouses/{warehouseId}/inventory` | SUPERVISOR | — | `List<InventoryResponseDTO>` | 200 | `supervisorPort.consultWarehouseInventory` | `ConsultWarehouseInventoryService` |
| GET | `/api/v1/supervisor/orders` | SUPERVISOR | — | `List<OrderResponseDTO>` | 200 | `supervisorPort.consultOrders` | `ConsultAllOrdersService` |
| GET | `/api/v1/supervisor/orders/{orderId}` | SUPERVISOR | — | `OrderResponseDTO` | 200 | `supervisorPort.consultOrder` | `ConsultOrderService` |
| GET | `/api/v1/supervisor/shipments` | SUPERVISOR | — | `List<ShipmentResponseDTO>` | 200 | `supervisorPort.consultShipments` | `ConsultShipmentsService` |
| GET | `/api/v1/supervisor/refunds` | SUPERVISOR | — | `List<RefundResponseDTO>` | 200 | `supervisorPort.consultRefunds` | `ConsultRefundsService` |
| GET | `/api/v1/supervisor/audit-logs` | SUPERVISOR | — | `AuditLogPageResponseDTO` | 200 | `supervisorPort.consultAuditLog` | `ConsultAuditLogsService` |
| GET | `/api/v1/supervisor/operations` | SUPERVISOR | — | `List<OperationResponseDTO>` | 200 | `supervisorPort.consultAllOperations` | `ConsultAllOperationsService` |

Parámetros de consulta: `GET /api/v1/supervisor/shipments` → `status`; `GET /api/v1/supervisor/refunds` → `status`; `GET /api/v1/supervisor/audit-logs` → `page, size, userId, operationType`

### Descripción de cada endpoint

- **GET `/api/v1/supervisor/summary`** — Construye las cifras consolidadas de solo lectura para la consulta administrativa (ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/users`** — Consulta los usuarios de la plataforma (ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/products`** — Consulta todos los productos del catálogo en cualquier estado (ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/warehouses`** — Consulta todas las bodegas (ADMIN, OPERADOR LOGÍSTICO y SUPERVISOR).
- **GET `/api/v1/supervisor/warehouses/{warehouseId}/inventory`** — Consulta el inventario almacenado en una bodega (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR; un VENDEDOR solo en sus propias bodegas).
- **GET `/api/v1/supervisor/orders`** — Consulta todos los pedidos del marketplace (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/orders/{orderId}`** — Consulta un pedido del marketplace (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/shipments`** — Consulta los envíos, opcionalmente filtrados por estado (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/refunds`** — Consulta los reembolsos, opcionalmente filtrados por estado (ADMIN y SUPERVISOR).
- **GET `/api/v1/supervisor/audit-logs`** — Consulta la traza de auditoría (ADMIN y SUPERVISOR). El filtro puede traer el usuario ejecutor y/o el tipo de operación; si vienen ambos, se aplican ambas condiciones.
- **GET `/api/v1/supervisor/operations`** — Consulta todas las operaciones registradas por el sistema (ADMIN y SUPERVISOR).

---
