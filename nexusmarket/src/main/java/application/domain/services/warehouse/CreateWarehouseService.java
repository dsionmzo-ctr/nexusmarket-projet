package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Operation;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Creates a MARKETPLACE or SELLER warehouse. Only an ADMIN can do it.
 */
public class CreateWarehouseService {

    private final AuthorizeOperationService authorizeOperationService;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public CreateWarehouseService(AuthorizeOperationService authorizeOperationService,
            WarehouseRepositoryPort warehouseRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Warehouse execute(User admin, Warehouse warehouse) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        warehouse.ensureValid();
        if (warehouse.getSeller() != null) {
            Seller seller = sellerRepositoryPort.findById(warehouse.getSeller())
                    .orElseThrow(() -> new EntityNotFoundException("Seller", warehouse.getSeller().getId()));
            warehouse.setSeller(seller);
        }
        warehouse.setStatus(WarehouseStatus.ACTIVE);
        Warehouse saved = warehouseRepositoryPort.save(warehouse);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.WAREHOUSE_CREATION, admin, saved, LocalDateTime.now(clock)),
                AuditDetails.of("name", saved.getName(), "type", saved.getType().getCode(),
                        "sellerId", saved.getSeller() == null ? null : saved.getSeller().getId()));
        return saved;
    }
}
