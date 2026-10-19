package application.domain.services.warehouse;

import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults every warehouse (ADMIN, LOGISTIC_OPERATOR and SUPERVISOR).
 */
public class ConsultWarehousesService {

    private final AuthorizeOperationService authorizeOperationService;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public ConsultWarehousesService(AuthorizeOperationService authorizeOperationService,
            WarehouseRepositoryPort warehouseRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public List<Warehouse> execute(User user) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.LOGISTIC_OPERATOR, SystemRole.SUPERVISOR);
        return warehouseRepositoryPort.findAll();
    }
}
