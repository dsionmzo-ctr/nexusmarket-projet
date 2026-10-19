package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.ports.in.SupervisorPort;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Read-only endpoints of the Supervisor role (SupervisorPort).
 */
@RestController
@RequestMapping("/api/v1/supervisor")
public class SupervisorRestController {

    private final SupervisorPort supervisorPort;

    public SupervisorRestController(SupervisorPort supervisorPort) {
        this.supervisorPort = supervisorPort;
    }

    /** Consults the consolidated administrative summary. */
    @GetMapping("/summary")
    public AdministrativeSummaryResponseDTO consultSummary(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return AdministrativeSummaryRestMapper.toResponse(supervisorPort.consultSummary(principal.domainUser()));
    }

    /** Lists the users. */
    @GetMapping("/users")
    public List<UserResponseDTO> consultUsers(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return supervisorPort.consultUsers(principal.domainUser()).stream().map(UserRestMapper::toResponse).toList();
    }

    /** Lists every product, whatever its status. */
    @GetMapping("/products")
    public List<ProductResponseDTO> consultProducts(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return supervisorPort.consultProducts(principal.domainUser()).stream().map(ProductRestMapper::toResponse).toList();
    }

    /** Lists the warehouses. */
    @GetMapping("/warehouses")
    public List<WarehouseResponseDTO> consultWarehouses(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return supervisorPort.consultWarehouses(principal.domainUser()).stream().map(WarehouseRestMapper::toResponse).toList();
    }

    /** Lists the inventory of a warehouse. */
    @GetMapping("/warehouses/{warehouseId}/inventory")
    public List<InventoryResponseDTO> consultWarehouseInventory(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long warehouseId) {
        return supervisorPort.consultWarehouseInventory(principal.domainUser(), WarehouseRestMapper.toProbe(warehouseId)).stream().map(InventoryRestMapper::toResponse).toList();
    }

    /** Lists all orders. */
    @GetMapping("/orders")
    public List<OrderResponseDTO> consultOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return supervisorPort.consultOrders(principal.domainUser()).stream().map(OrderRestMapper::toResponse).toList();
    }

    /** Consults an order. */
    @GetMapping("/orders/{orderId}")
    public OrderResponseDTO consultOrder(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return OrderRestMapper.toResponse(supervisorPort.consultOrder(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Lists shipments, optionally filtered by status. */
    @GetMapping("/shipments")
    public List<ShipmentResponseDTO> consultShipments(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @RequestParam(required = false) @Size(max = 40) String status) {
        return supervisorPort.consultShipments(principal.domainUser(), ShipmentRestMapper.toFilter(status)).stream().map(ShipmentRestMapper::toResponse).toList();
    }

    /** Lists refunds, optionally filtered by status. */
    @GetMapping("/refunds")
    public List<RefundResponseDTO> consultRefunds(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @RequestParam(required = false) @Size(max = 40) String status) {
        return supervisorPort.consultRefunds(principal.domainUser(), RefundRestMapper.toFilter(status)).stream().map(RefundRestMapper::toResponse).toList();
    }

    /** Pages the audit log (page >= 0, size 1..100, default 20), optionally by performer and operation type. */
    @GetMapping("/audit-logs")
    public AuditLogPageResponseDTO consultAuditLog(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @RequestParam(defaultValue = "0") @PositiveOrZero int page, @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size, @RequestParam(required = false) @Positive Long userId, @RequestParam(required = false) @Size(max = 40) String operationType) {
        return AuditLogRestMapper.toPage(supervisorPort.consultAuditLog(principal.domainUser(), AuditLogRestMapper.toFilter(userId, operationType)), page, size);
    }

    /** Lists all the operations of the marketplace. */
    @GetMapping("/operations")
    public List<OperationResponseDTO> consultAllOperations(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return supervisorPort.consultAllOperations(principal.domainUser()).stream().map(OperationRestMapper::toResponse).toList();
    }
}
