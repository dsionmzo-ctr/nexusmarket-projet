package application.adapters.useCases;

import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.ports.in.LogisticOperatorPort;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ConsultInventoryMovementsService;
import application.domain.services.inventory.ConsultWarehouseInventoryService;
import application.domain.services.inventory.MarkInventoryAvailableService;
import application.domain.services.inventory.MarkInventoryDamagedService;
import application.domain.services.inventory.RegisterInventoryInflowService;
import application.domain.services.operation.ConsultUserOperationsService;
import application.domain.services.order.ConsultAllOrdersService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.returns.ConsultReturnsToReceiveService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.returns.RejectReturnService;
import application.domain.services.shipping.ConfirmShipmentDeliveryService;
import application.domain.services.shipping.ConsultShipmentsService;
import application.domain.services.shipping.DispatchShipmentService;
import application.domain.services.warehouse.ConsultWarehousesService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case adapter of the Logistic Operator role: warehouses, inventory, shipments and returns reception.
 * Delegates every operation to the single-responsibility domain services; it holds no business rule.
 */
@Service
@Transactional
public class LogisticOperatorUseCaseImpl implements LogisticOperatorPort {

    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultWarehouseInventoryService consultWarehouseInventoryService;
    private final ConsultInventoryMovementsService consultInventoryMovementsService;
    private final RegisterInventoryInflowService registerInventoryInflowService;
    private final AdjustInventoryService adjustInventoryService;
    private final MarkInventoryDamagedService markInventoryDamagedService;
    private final MarkInventoryAvailableService markInventoryAvailableService;
    private final ConsultAllOrdersService consultAllOrdersService;
    private final ConsultOrderService consultOrderService;
    private final ConsultShipmentsService consultShipmentsService;
    private final DispatchShipmentService dispatchShipmentService;
    private final ConfirmShipmentDeliveryService confirmShipmentDeliveryService;
    private final ConsultReturnsToReceiveService consultReturnsToReceiveService;
    private final ReceiveReturnService receiveReturnService;
    private final RejectReturnService rejectReturnService;
    private final ConsultUserOperationsService consultUserOperationsService;

    public LogisticOperatorUseCaseImpl(ConsultWarehousesService consultWarehousesService,
            ConsultWarehouseInventoryService consultWarehouseInventoryService,
            ConsultInventoryMovementsService consultInventoryMovementsService,
            RegisterInventoryInflowService registerInventoryInflowService,
            AdjustInventoryService adjustInventoryService,
            MarkInventoryDamagedService markInventoryDamagedService,
            MarkInventoryAvailableService markInventoryAvailableService,
            ConsultAllOrdersService consultAllOrdersService,
            ConsultOrderService consultOrderService,
            ConsultShipmentsService consultShipmentsService,
            DispatchShipmentService dispatchShipmentService,
            ConfirmShipmentDeliveryService confirmShipmentDeliveryService,
            ConsultReturnsToReceiveService consultReturnsToReceiveService,
            ReceiveReturnService receiveReturnService,
            RejectReturnService rejectReturnService,
            ConsultUserOperationsService consultUserOperationsService) {
        this.consultWarehousesService = consultWarehousesService;
        this.consultWarehouseInventoryService = consultWarehouseInventoryService;
        this.consultInventoryMovementsService = consultInventoryMovementsService;
        this.registerInventoryInflowService = registerInventoryInflowService;
        this.adjustInventoryService = adjustInventoryService;
        this.markInventoryDamagedService = markInventoryDamagedService;
        this.markInventoryAvailableService = markInventoryAvailableService;
        this.consultAllOrdersService = consultAllOrdersService;
        this.consultOrderService = consultOrderService;
        this.consultShipmentsService = consultShipmentsService;
        this.dispatchShipmentService = dispatchShipmentService;
        this.confirmShipmentDeliveryService = confirmShipmentDeliveryService;
        this.consultReturnsToReceiveService = consultReturnsToReceiveService;
        this.receiveReturnService = receiveReturnService;
        this.rejectReturnService = rejectReturnService;
        this.consultUserOperationsService = consultUserOperationsService;
    }

    @Override
    public List<Warehouse> consultWarehouses(User user) {
        return consultWarehousesService.execute(user);
    }

    @Override
    public List<Inventory> consultWarehouseInventory(User user, Warehouse warehouse) {
        return consultWarehouseInventoryService.execute(user, warehouse);
    }

    @Override
    public List<InventoryMovement> consultInventoryMovements(User user, Inventory inventory) {
        return consultInventoryMovementsService.execute(user, inventory);
    }

    @Override
    public Inventory registerInventoryInflow(User user, InventoryMovement movement) {
        return registerInventoryInflowService.execute(user, movement);
    }

    @Override
    public Inventory adjustInventory(User user, InventoryMovement movement) {
        return adjustInventoryService.execute(user, movement);
    }

    @Override
    public Inventory markInventoryDamaged(User user, InventoryMovement movement) {
        return markInventoryDamagedService.execute(user, movement);
    }

    @Override
    public Inventory markInventoryAvailable(User user, InventoryMovement movement) {
        return markInventoryAvailableService.execute(user, movement);
    }

    @Override
    public List<Order> consultOrders(User user) {
        return consultAllOrdersService.execute(user);
    }

    @Override
    public Order consultOrder(User user, Order order) {
        return consultOrderService.execute(user, order);
    }

    @Override
    public List<Shipment> consultShipments(User user, Shipment filter) {
        return consultShipmentsService.execute(user, filter);
    }

    @Override
    public Shipment dispatchShipment(User user, Shipment shipment) {
        return dispatchShipmentService.execute(user, shipment);
    }

    @Override
    public Shipment confirmDelivery(User user, Shipment shipment) {
        return confirmShipmentDeliveryService.execute(user, shipment);
    }

    @Override
    public List<ProductReturn> consultReturnsToReceive(User user) {
        return consultReturnsToReceiveService.execute(user);
    }

    @Override
    public ProductReturn receiveReturn(User user, ProductReturn productReturn) {
        return receiveReturnService.execute(user, productReturn);
    }

    @Override
    public ProductReturn rejectReturn(User user, ProductReturn productReturn) {
        return rejectReturnService.execute(user, productReturn);
    }

    @Override
    public List<Operation> consultMyOperations(User user) {
        return consultUserOperationsService.execute(user);
    }
}
