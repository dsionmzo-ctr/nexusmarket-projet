package application.domain.services.returns;

import application.domain.exceptions.InvalidWarehouseException;
import application.domain.models.Operation;
import application.domain.models.ProductReturn;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.ProductReturnRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.warehouse.FindWarehouseService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A LOGISTIC_OPERATOR receives the returned goods in a warehouse. The units re-enter the inventory with a
 * RETURN movement.
 */
public class ReceiveReturnService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindProductReturnService findProductReturnService;
    private final FindWarehouseService findWarehouseService;
    private final RestockReturnedGoodsService restockReturnedGoodsService;
    private final ProductReturnRepositoryPort productReturnRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ReceiveReturnService(AuthorizeOperationService authorizeOperationService,
            FindProductReturnService findProductReturnService,
            FindWarehouseService findWarehouseService,
            RestockReturnedGoodsService restockReturnedGoodsService,
            ProductReturnRepositoryPort productReturnRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findProductReturnService = findProductReturnService;
        this.findWarehouseService = findWarehouseService;
        this.restockReturnedGoodsService = restockReturnedGoodsService;
        this.productReturnRepositoryPort = productReturnRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public ProductReturn execute(User user, ProductReturn requested) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR);
        ProductReturn productReturn = findProductReturnService.execute(requested);
        Warehouse warehouse = findWarehouseService.execute(requested.getReceivedWarehouse());
        if (!warehouse.isActive()) {
            throw new InvalidWarehouseException("The warehouse is not active.");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        productReturn.receive(warehouse, now);
        restockReturnedGoodsService.execute(user, productReturn);
        productReturnRepositoryPort.update(productReturn);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.RETURN_RECEIPT, user, productReturn, now),
                AuditDetails.of("warehouseId", warehouse.getId(), "quantity", productReturn.getQuantity()));
        return productReturn;
    }
}
