package application.domain.models;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

/**
 * Physical storage place. It is either a marketplace warehouse (no seller) or a seller warehouse
 * (owned by exactly one seller).
 */
public class Warehouse extends MarketplaceResource {

    private String name;
    private WarehouseType type;
    private Address address;
    private Seller seller;
    private WarehouseStatus status = WarehouseStatus.ACTIVE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WarehouseType getType() {
        return type;
    }

    public void setType(WarehouseType type) {
        this.type = type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public WarehouseStatus getStatus() {
        return status;
    }

    public void setStatus(WarehouseStatus status) {
        this.status = status;
    }


    @Override
    public String getResourceType() {
        return "WAREHOUSE";
    }

    public void ensureValid() {
        if (name == null || name.isBlank()) {
            throw new InvalidWarehouseException("Warehouse name must be provided.");
        }
        if (type == null) {
            throw new InvalidWarehouseException("Warehouse type must be provided.");
        }
        if (address == null) {
            throw new InvalidWarehouseException("Warehouse address must be provided.");
        }
        if (WarehouseType.SELLER.equals(type) && seller == null) {
            throw new InvalidWarehouseException("A seller warehouse must belong to a seller.");
        }
        if (WarehouseType.MARKETPLACE.equals(type) && seller != null) {
            throw new InvalidWarehouseException("A marketplace warehouse cannot belong to a seller.");
        }
    }

    public void update(String newName, Address newAddress) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
        if (newAddress != null) {
            this.address = newAddress;
        }
    }

    public boolean isActive() {
        return WarehouseStatus.ACTIVE.equals(status);
    }

    public void changeStatus(WarehouseStatus target) {
        if (target == null || target.equals(status)) {
            throw new InvalidStatusTransitionException("Warehouse", status, target);
        }
        this.status = target;
    }

    public boolean isOwnedBy(Seller candidate) {
        return seller != null && candidate != null && seller.getId() != null && seller.getId().equals(candidate.getId());
    }

}
