package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Loads a warehouse by its identifier or fails with EntityNotFoundException.
 */
public class FindWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public FindWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public Warehouse execute(Warehouse probe) {
        if (probe == null || probe.getId() == null) {
            throw new InvalidWarehouseException("Warehouse identifier must be provided.");
        }
        return warehouseRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", probe.getId()));
    }
}
