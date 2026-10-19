package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOrderException;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;

/**
 * Loads an order by its identifier or fails with EntityNotFoundException.
 */
public class FindOrderService {

    private final OrderRepositoryPort orderRepositoryPort;

    public FindOrderService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Order execute(Order probe) {
        if (probe == null || probe.getId() == null) {
            throw new InvalidOrderException("Order identifier must be provided.");
        }
        return orderRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("Order", probe.getId()));
    }
}
