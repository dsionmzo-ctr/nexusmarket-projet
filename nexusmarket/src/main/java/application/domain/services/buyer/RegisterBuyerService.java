package application.domain.services.buyer;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;

/**
 * Self-registration of a buyer: validates the mandatory data (identification, name, e-mail, primary address)
 * and the uniqueness of the identification and the e-mail in the platform. It is an anonymous operation, so
 * the Operation/Audit trail is recorded when the buyer account (user) is created.
 */
public class RegisterBuyerService {

    private final BuyerRepositoryPort buyerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public RegisterBuyerService(BuyerRepositoryPort buyerRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    public Buyer execute(Buyer buyer) {
        if (buyer == null) {
            throw new InvalidUserException("Buyer must be provided.");
        }
        buyer.setRole(SystemRole.BUYER);
        buyer.setStatus(BuyerStatus.ENABLED);
        buyer.ensureComplete();
        User probe = new User();
        probe.setIdentification(buyer.getIdentification());
        probe.setEmail(buyer.getEmail());
        if (buyerRepositoryPort.existsByIdentification(buyer) || buyerRepositoryPort.existsByEmail(buyer)
                || userRepositoryPort.existsByIdentification(probe) || userRepositoryPort.existsByEmail(probe)) {
            throw new DuplicateResourceException("Identification or e-mail already registered.");
        }
        return buyerRepositoryPort.save(buyer);
    }
}
