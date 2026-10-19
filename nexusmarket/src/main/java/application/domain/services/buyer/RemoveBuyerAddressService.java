package application.domain.services.buyer;

import application.domain.models.Buyer;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Removes an additional delivery address from the profile of the authenticated buyer
 * (the primary address cannot be removed).
 */
public class RemoveBuyerAddressService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RemoveBuyerAddressService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            BuyerRepositoryPort buyerRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Buyer execute(User user, Address address) {
        Buyer buyer = resolveAuthenticatedBuyerService.execute(user);
        buyer.removeAddress(address);
        buyerRepositoryPort.update(buyer);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.BUYER_ADDRESS_REMOVAL, user, buyer, LocalDateTime.now(clock)),
                AuditDetails.of("city", address.city(), "country", address.country()));
        return buyer;
    }
}
