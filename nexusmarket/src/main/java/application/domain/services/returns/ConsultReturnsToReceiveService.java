package application.domain.services.returns;

import application.domain.models.ProductReturn;
import application.domain.models.User;
import application.domain.ports.out.ProductReturnRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * A LOGISTIC_OPERATOR consults the returns waiting to be received (status REQUESTED).
 */
public class ConsultReturnsToReceiveService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ProductReturnRepositoryPort productReturnRepositoryPort;

    public ConsultReturnsToReceiveService(AuthorizeOperationService authorizeOperationService,
            ProductReturnRepositoryPort productReturnRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.productReturnRepositoryPort = productReturnRepositoryPort;
    }

    public List<ProductReturn> execute(User user) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR);
        ProductReturn criteria = new ProductReturn();
        criteria.setStatus(ReturnStatus.REQUESTED);
        return productReturnRepositoryPort.findByStatus(criteria);
    }
}
