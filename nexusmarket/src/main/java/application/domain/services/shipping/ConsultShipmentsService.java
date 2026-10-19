package application.domain.services.shipping;

import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the shipments, optionally filtered by status (LOGISTIC_OPERATOR, ADMIN and SUPERVISOR).
 */
public class ConsultShipmentsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public ConsultShipmentsService(AuthorizeOperationService authorizeOperationService,
            ShipmentRepositoryPort shipmentRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    public List<Shipment> execute(User user, Shipment filter) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return shipmentRepositoryPort.findByStatus(filter == null ? new Shipment() : filter);
    }
}
