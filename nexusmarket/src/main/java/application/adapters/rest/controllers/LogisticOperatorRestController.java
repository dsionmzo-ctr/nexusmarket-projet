package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.ports.in.LogisticOperatorPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints of the Logistic Operator role (LogisticOperatorPort): warehouses, inventory, shipments and returns.
 */
@RestController
@RequestMapping("/api/v1/logistic-operator")
public class LogisticOperatorRestController {

    private final LogisticOperatorPort logisticOperatorPort;

    public LogisticOperatorRestController(LogisticOperatorPort logisticOperatorPort) {
        this.logisticOperatorPort = logisticOperatorPort;
    }

    /** Lists the warehouses. */
    @GetMapping("/warehouses")
    public List<WarehouseResponseDTO> consultWarehouses(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return logisticOperatorPort.consultWarehouses(principal.domainUser()).stream().map(WarehouseRestMapper::toResponse).toList();
    }

    /** Lists the inventory of a warehouse. */
    @GetMapping("/warehouses/{warehouseId}/inventory")
    public List<InventoryResponseDTO> consultWarehouseInventory(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long warehouseId) {
        return logisticOperatorPort.consultWarehouseInventory(principal.domainUser(), WarehouseRestMapper.toProbe(warehouseId)).stream().map(InventoryRestMapper::toResponse).toList();
    }

    /** Lists the movement history of an inventory record. */
    @GetMapping("/inventory/{inventoryId}/movements")
    public List<InventoryMovementResponseDTO> consultInventoryMovements(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long inventoryId) {
        return logisticOperatorPort.consultInventoryMovements(principal.domainUser(), InventoryRestMapper.toProbe(inventoryId)).stream().map(InventoryRestMapper::toResponse).toList();
    }

    /** Registers an inflow of units into a warehouse. */
    @PostMapping("/inventory/inflows")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDTO registerInventoryInflow(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody InventoryInflowRequestDTO request) {
        return InventoryRestMapper.toResponse(logisticOperatorPort.registerInventoryInflow(principal.domainUser(), InventoryRestMapper.toInflow(request)));
    }

    /** Adjusts the on-hand units to the counted quantity. */
    @PatchMapping("/inventory/{inventoryId}/adjustment")
    public InventoryResponseDTO adjustInventory(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long inventoryId, @Valid @RequestBody InventoryAdjustmentRequestDTO request) {
        return InventoryRestMapper.toResponse(logisticOperatorPort.adjustInventory(principal.domainUser(), InventoryRestMapper.toAdjustment(inventoryId, request)));
    }

    /** Marks an inventory record as damaged (it can no longer be reserved). */
    @PatchMapping("/inventory/{inventoryId}/damaged")
    public InventoryResponseDTO markInventoryDamaged(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long inventoryId, @Valid @RequestBody(required = false) InventoryNoteRequestDTO request) {
        return InventoryRestMapper.toResponse(logisticOperatorPort.markInventoryDamaged(principal.domainUser(), InventoryRestMapper.toNote(inventoryId, request)));
    }

    /** Restores a damaged inventory record to available. */
    @PatchMapping("/inventory/{inventoryId}/available")
    public InventoryResponseDTO markInventoryAvailable(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long inventoryId, @Valid @RequestBody(required = false) InventoryNoteRequestDTO request) {
        return InventoryRestMapper.toResponse(logisticOperatorPort.markInventoryAvailable(principal.domainUser(), InventoryRestMapper.toNote(inventoryId, request)));
    }

    /** Lists all orders. */
    @GetMapping("/orders")
    public List<OrderResponseDTO> consultOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return logisticOperatorPort.consultOrders(principal.domainUser()).stream().map(OrderRestMapper::toResponse).toList();
    }

    /** Consults an order. */
    @GetMapping("/orders/{orderId}")
    public OrderResponseDTO consultOrder(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return OrderRestMapper.toResponse(logisticOperatorPort.consultOrder(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Lists shipments, optionally filtered by status. */
    @GetMapping("/shipments")
    public List<ShipmentResponseDTO> consultShipments(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @RequestParam(required = false) @Size(max = 40) String status) {
        return logisticOperatorPort.consultShipments(principal.domainUser(), ShipmentRestMapper.toFilter(status)).stream().map(ShipmentRestMapper::toResponse).toList();
    }

    /** Dispatches a prepared shipment. */
    @PatchMapping("/shipments/{shipmentId}/dispatch")
    public ShipmentResponseDTO dispatchShipment(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long shipmentId) {
        return ShipmentRestMapper.toResponse(logisticOperatorPort.dispatchShipment(principal.domainUser(), ShipmentRestMapper.toProbe(shipmentId)));
    }

    /** Confirms the delivery of a dispatched shipment (closes the order). */
    @PatchMapping("/shipments/{shipmentId}/delivery")
    public ShipmentResponseDTO confirmDelivery(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long shipmentId) {
        return ShipmentRestMapper.toResponse(logisticOperatorPort.confirmDelivery(principal.domainUser(), ShipmentRestMapper.toProbe(shipmentId)));
    }

    /** Lists the returns waiting for reception. */
    @GetMapping("/returns")
    public List<ProductReturnResponseDTO> consultReturnsToReceive(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return logisticOperatorPort.consultReturnsToReceive(principal.domainUser()).stream().map(ReturnRestMapper::toResponse).toList();
    }

    /** Receives returned goods in a warehouse and restocks them. */
    @PatchMapping("/returns/{returnId}/reception")
    public ProductReturnResponseDTO receiveReturn(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long returnId, @Valid @RequestBody ReceiveReturnRequestDTO request) {
        return ReturnRestMapper.toResponse(logisticOperatorPort.receiveReturn(principal.domainUser(), ReturnRestMapper.toReception(returnId, request)));
    }

    /** Rejects a return. */
    @PatchMapping("/returns/{returnId}/rejection")
    public ProductReturnResponseDTO rejectReturn(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long returnId) {
        return ReturnRestMapper.toResponse(logisticOperatorPort.rejectReturn(principal.domainUser(), ReturnRestMapper.toProbe(returnId)));
    }

    /** Lists the operations executed by the operator. */
    @GetMapping("/operations")
    public List<OperationResponseDTO> consultMyOperations(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return logisticOperatorPort.consultMyOperations(principal.domainUser()).stream().map(OperationRestMapper::toResponse).toList();
    }
}
