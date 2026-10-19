# Adaptadores de casos de uso

## Introducción

Los adaptadores `*UseCaseImpl` (`application.adapters.useCases`) **implementan los puertos de entrada**. Son la frontera transaccional de la aplicación y se limitan a **delegar** cada método en el servicio de dominio que resuelve ese caso de uso.

## Reglas

- Cada clase se anota `@Service` y `@Transactional` (la transacción abarca el caso de uso completo: cambios de negocio y `Operation` en PostgreSQL).
- No contienen lógica de negocio ni de autorización; solo inyectan servicios por constructor y los invocan.
- Una llamada de puerto = un servicio de dominio (o, cuando el caso de uso coordina varios pasos, el servicio orquestador que corresponde, como `IncorporateSellerService`).
- **Atomicidad entre almacenes:** el `AuditLog` se guarda en MongoDB y la `Operation` en PostgreSQL. Son almacenes distintos y **no comparten transacción**: si MongoDB falla después de confirmar PostgreSQL, el cambio de negocio queda y la auditoría puede perder el registro. Es una limitación conocida y documentada (ver `Supuestos-y-vacios.md`).

## Mapeo puerto → servicio de dominio

### PublicAccessPort → `PublicAccessUseCaseImpl`

| Método | Servicio(s) invocado(s) |
|---|---|
| `login` | `LoginService` |
| `logout` | `LogoutService` |
| `registerBuyer` | `RegisterBuyerService` |
| `registerBuyerUser` | `RegisterBuyerUserService` |
| `consultPublishedProducts` | `ConsultPublishedProductsService` |
| `consultPublishedProduct` | `ConsultPublishedProductService` |

### BuyerPort → `BuyerUseCaseImpl`

| Método | Servicio(s) invocado(s) |
|---|---|
| `consultMyProfile` | `ConsultBuyerProfileService` |
| `addAddress` | `AddBuyerAddressService` |
| `removeAddress` | `RemoveBuyerAddressService` |
| `consultMyCart` | `ConsultCartService` |
| `addItemToCart` | `AddItemToCartService` |
| `updateCartItem` | `UpdateCartItemQuantityService` |
| `removeCartItem` | `RemoveCartItemService` |
| `clearCart` | `ClearCartService` |
| `checkout` | `CheckoutService` |
| `payOrder` | `PayOrderService` |
| `cancelOrder` | `CancelOrderService` |
| `consultMyOrders` | `ConsultBuyerOrdersService` |
| `consultMyOrder` | `FindBuyerOrderService` |
| `consultMyInvoice` | `ConsultOrderInvoiceService` |
| `consultMyShipment` | `ConsultOrderShipmentService` |
| `requestReturn` | `RequestReturnService` |
| `consultMyReturns` | `ConsultBuyerReturnsService` |
| `consultMyRefunds` | `ConsultBuyerRefundsService` |
| `consultMyOperations` | `ConsultUserOperationsService` |

### SellerPort → `SellerUseCaseImpl`

| Método | Servicio(s) invocado(s) |
|---|---|
| `registerProduct` | `RegisterProductService` |
| `updateProduct` | `UpdateProductService` |
| `addProductVariant` | `AddProductVariantService` |
| `publishProduct` | `PublishProductService` |
| `suspendProduct` | `SuspendProductService` |
| `discontinueProduct` | `DiscontinueProductService` |
| `consultMyProducts` | `ConsultSellerProductsService` |
| `consultMyWarehouses` | `ConsultSellerWarehousesService` |
| `registerInventoryInflow` | `RegisterInventoryInflowService` |
| `consultMyInventory` | `ConsultSellerInventoryService` |
| `consultMyOrders` | `ConsultSellerOrdersService` |
| `consultMyOrder` | `ConsultSellerOrderService` |
| `consultMyOperations` | `ConsultUserOperationsService` |

### LogisticOperatorPort → `LogisticOperatorUseCaseImpl`

| Método | Servicio(s) invocado(s) |
|---|---|
| `consultWarehouses` | `ConsultWarehousesService` |
| `consultWarehouseInventory` | `ConsultWarehouseInventoryService` |
| `consultInventoryMovements` | `ConsultInventoryMovementsService` |
| `registerInventoryInflow` | `RegisterInventoryInflowService` |
| `adjustInventory` | `AdjustInventoryService` |
| `markInventoryDamaged` | `MarkInventoryDamagedService` |
| `markInventoryAvailable` | `MarkInventoryAvailableService` |
| `consultOrders` | `ConsultAllOrdersService` |
| `consultOrder` | `ConsultOrderService` |
| `consultShipments` | `ConsultShipmentsService` |
| `dispatchShipment` | `DispatchShipmentService` |
| `confirmDelivery` | `ConfirmShipmentDeliveryService` |
| `consultReturnsToReceive` | `ConsultReturnsToReceiveService` |
| `receiveReturn` | `ReceiveReturnService` |
| `rejectReturn` | `RejectReturnService` |
| `consultMyOperations` | `ConsultUserOperationsService` |

### AdminPort → `AdminUseCaseImpl`

| Método | Servicio(s) invocado(s) |
|---|---|
| `registerSeller` | `IncorporateSellerService` |
| `registerSellerUser` | `RegisterSellerUserService` |
| `registerStaffUser` | `RegisterStaffUserService` |
| `changeUserStatus` | `ChangeUserStatusService` |
| `consultUsers` | `ConsultUsersService` |
| `changeBuyerStatus` | `ChangeBuyerStatusService` |
| `createWarehouse` | `CreateWarehouseService` |
| `updateWarehouse` | `UpdateWarehouseService` |
| `changeWarehouseStatus` | `ChangeWarehouseStatusService` |
| `consultWarehouses` | `ConsultWarehousesService` |
| `consultPendingRefunds` | `ConsultRefundsService` |
| `approveRefund` | `ApproveRefundService` |
| `rejectRefund` | `RejectRefundService` |
| `consultAuditLog` | `ConsultAuditLogsService` |
| `consultAllOperations` | `ConsultAllOperationsService` |
| `consultSummary` | `ConsultAdministrativeSummaryService` |

### SupervisorPort → `SupervisorUseCaseImpl`

| Método | Servicio(s) invocado(s) |
|---|---|
| `consultSummary` | `ConsultAdministrativeSummaryService` |
| `consultUsers` | `ConsultUsersService` |
| `consultProducts` | `ConsultAllProductsService` |
| `consultWarehouses` | `ConsultWarehousesService` |
| `consultWarehouseInventory` | `ConsultWarehouseInventoryService` |
| `consultOrders` | `ConsultAllOrdersService` |
| `consultOrder` | `ConsultOrderService` |
| `consultShipments` | `ConsultShipmentsService` |
| `consultRefunds` | `ConsultRefundsService` |
| `consultAuditLog` | `ConsultAuditLogsService` |
| `consultAllOperations` | `ConsultAllOperationsService` |
