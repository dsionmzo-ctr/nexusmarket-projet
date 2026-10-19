package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.ports.in.AdminPort;
import jakarta.validation.Valid;
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
 * Endpoints of the Admin role (AdminPort): sellers, staff users, warehouses, refund decisions and audit.
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private final AdminPort adminPort;

    public AdminRestController(AdminPort adminPort) {
        this.adminPort = adminPort;
    }

    /** Incorporates a seller together with its first warehouse. */
    @PostMapping("/sellers")
    @ResponseStatus(HttpStatus.CREATED)
    public SellerResponseDTO registerSeller(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody RegisterSellerRequestDTO request) {
        return SellerRestMapper.toResponse(adminPort.registerSeller(principal.domainUser(), SellerRestMapper.toDomain(request), SellerRestMapper.toFirstWarehouse(request.firstWarehouse())));
    }

    /** Creates the access account of a seller. */
    @PostMapping("/seller-users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO registerSellerUser(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody RegisterSellerUserRequestDTO request) {
        return UserRestMapper.toResponse(adminPort.registerSellerUser(principal.domainUser(), UserRestMapper.toSellerUser(request)));
    }

    /** Creates a logistic operator, supervisor or admin user. */
    @PostMapping("/staff-users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO registerStaffUser(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody RegisterStaffUserRequestDTO request) {
        return UserRestMapper.toResponse(adminPort.registerStaffUser(principal.domainUser(), UserRestMapper.toStaffUser(request)));
    }

    /** Lists the users. */
    @GetMapping("/users")
    public List<UserResponseDTO> consultUsers(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return adminPort.consultUsers(principal.domainUser()).stream().map(UserRestMapper::toResponse).toList();
    }

    /** Changes the status of a user (ACTIVE, INACTIVE, BLOCKED). */
    @PatchMapping("/users/{userId}/status")
    public UserResponseDTO changeUserStatus(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long userId, @Valid @RequestBody ChangeStatusRequestDTO request) {
        return UserRestMapper.toResponse(adminPort.changeUserStatus(principal.domainUser(), UserRestMapper.toStatusTarget(userId), UserRestMapper.toStatus(request.status())));
    }

    /** Changes the commercial status of a buyer (ENABLED, RESTRICTED). */
    @PatchMapping("/buyers/{buyerId}/status")
    public BuyerResponseDTO changeBuyerStatus(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long buyerId, @Valid @RequestBody ChangeStatusRequestDTO request) {
        return BuyerRestMapper.toResponse(adminPort.changeBuyerStatus(principal.domainUser(), BuyerRestMapper.toProbe(buyerId), BuyerRestMapper.toStatus(request.status())));
    }

    /** Creates a warehouse. */
    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponseDTO createWarehouse(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody CreateWarehouseRequestDTO request) {
        return WarehouseRestMapper.toResponse(adminPort.createWarehouse(principal.domainUser(), WarehouseRestMapper.toDomain(request)));
    }

    /** Updates the name and address of a warehouse. */
    @PutMapping("/warehouses/{warehouseId}")
    public WarehouseResponseDTO updateWarehouse(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long warehouseId, @Valid @RequestBody UpdateWarehouseRequestDTO request) {
        return WarehouseRestMapper.toResponse(adminPort.updateWarehouse(principal.domainUser(), WarehouseRestMapper.toChanges(warehouseId, request)));
    }

    /** Activates or deactivates a warehouse. */
    @PatchMapping("/warehouses/{warehouseId}/status")
    public WarehouseResponseDTO changeWarehouseStatus(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long warehouseId, @Valid @RequestBody ChangeStatusRequestDTO request) {
        return WarehouseRestMapper.toResponse(adminPort.changeWarehouseStatus(principal.domainUser(), WarehouseRestMapper.toProbe(warehouseId), WarehouseRestMapper.toStatus(request.status())));
    }

    /** Lists the warehouses. */
    @GetMapping("/warehouses")
    public List<WarehouseResponseDTO> consultWarehouses(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return adminPort.consultWarehouses(principal.domainUser()).stream().map(WarehouseRestMapper::toResponse).toList();
    }

    /** Lists the refunds waiting for a decision. */
    @GetMapping("/refunds/pending")
    public List<RefundResponseDTO> consultPendingRefunds(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return adminPort.consultPendingRefunds(principal.domainUser()).stream().map(RefundRestMapper::toResponse).toList();
    }

    /** Approves a refund. */
    @PatchMapping("/refunds/{refundId}/approval")
    public RefundResponseDTO approveRefund(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long refundId, @Valid @RequestBody(required = false) RefundDecisionRequestDTO request) {
        return RefundRestMapper.toResponse(adminPort.approveRefund(principal.domainUser(), RefundRestMapper.toDecision(refundId, request)));
    }

    /** Rejects a refund. */
    @PatchMapping("/refunds/{refundId}/rejection")
    public RefundResponseDTO rejectRefund(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long refundId, @Valid @RequestBody(required = false) RefundDecisionRequestDTO request) {
        return RefundRestMapper.toResponse(adminPort.rejectRefund(principal.domainUser(), RefundRestMapper.toDecision(refundId, request)));
    }

    /** Pages the audit log (page >= 0, size 1..100, default 20), optionally by performer and operation type. */
    @GetMapping("/audit-logs")
    public AuditLogPageResponseDTO consultAuditLog(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @RequestParam(defaultValue = "0") @PositiveOrZero int page, @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size, @RequestParam(required = false) @Positive Long userId, @RequestParam(required = false) @Size(max = 40) String operationType) {
        return AuditLogRestMapper.toPage(adminPort.consultAuditLog(principal.domainUser(), AuditLogRestMapper.toFilter(userId, operationType)), page, size);
    }

    /** Lists all the operations of the marketplace. */
    @GetMapping("/operations")
    public List<OperationResponseDTO> consultAllOperations(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return adminPort.consultAllOperations(principal.domainUser()).stream().map(OperationRestMapper::toResponse).toList();
    }

    /** Consults the consolidated administrative summary. */
    @GetMapping("/summary")
    public AdministrativeSummaryResponseDTO consultSummary(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return AdministrativeSummaryRestMapper.toResponse(adminPort.consultSummary(principal.domainUser()));
    }
}
