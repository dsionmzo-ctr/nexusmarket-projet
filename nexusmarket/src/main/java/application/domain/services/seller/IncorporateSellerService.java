package application.domain.services.seller;

import application.domain.exceptions.InvalidWarehouseException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.warehouse.CreateWarehouseService;
import application.domain.valueobjects.WarehouseType;

/**
 * Coordinates the incorporation flow of the business: the ADMIN registers the seller and its first
 * warehouse (the warehouse is created for the new seller). It is executed in one transaction.
 */
public class IncorporateSellerService {

    private final RegisterSellerService registerSellerService;
    private final CreateWarehouseService createWarehouseService;

    public IncorporateSellerService(RegisterSellerService registerSellerService,
            CreateWarehouseService createWarehouseService) {
        this.registerSellerService = registerSellerService;
        this.createWarehouseService = createWarehouseService;
    }

    public Seller execute(User admin, Seller seller, Warehouse firstWarehouse) {
        if (firstWarehouse == null) {
            throw new InvalidWarehouseException("The first warehouse of the seller must be provided.");
        }
        Seller saved = registerSellerService.execute(admin, seller);
        firstWarehouse.setType(WarehouseType.SELLER);
        firstWarehouse.setSeller(saved);
        createWarehouseService.execute(admin, firstWarehouse);
        return saved;
    }
}
