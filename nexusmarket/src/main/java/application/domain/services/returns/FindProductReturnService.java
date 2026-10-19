package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidReturnException;
import application.domain.models.ProductReturn;
import application.domain.ports.out.ProductReturnRepositoryPort;

/**
 * Loads a product return by its identifier or fails with EntityNotFoundException.
 */
public class FindProductReturnService {

    private final ProductReturnRepositoryPort productReturnRepositoryPort;

    public FindProductReturnService(ProductReturnRepositoryPort productReturnRepositoryPort) {
        this.productReturnRepositoryPort = productReturnRepositoryPort;
    }

    public ProductReturn execute(ProductReturn probe) {
        if (probe == null || probe.getId() == null) {
            throw new InvalidReturnException("Return identifier must be provided.");
        }
        return productReturnRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("Return", probe.getId()));
    }
}
