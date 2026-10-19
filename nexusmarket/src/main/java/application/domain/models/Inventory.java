package application.domain.models;

import application.domain.exceptions.DamagedInventoryException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidInventoryException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.InventoryStatus;

/**
 * Distributed stock of one product variant in one warehouse.
 *
 * Invariants: stock is never negative, reserved stock never exceeds the stock on hand and
 * damaged stock can never be reserved.
 */
public class Inventory extends MarketplaceResource {

    private Product product;
    private ProductVariant variant;
    private Warehouse warehouse;
    private int onHand;
    private int reserved;
    private InventoryStatus status = InventoryStatus.AVAILABLE;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public int getOnHand() {
        return onHand;
    }

    public void setOnHand(int onHand) {
        this.onHand = onHand;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }


    @Override
    public String getResourceType() {
        return "INVENTORY";
    }

    /** Units that can still be reserved. */
    public int available() {
        return onHand - reserved;
    }

    /** INFLOW: stock entering the warehouse. */
    public void receive(int quantity) {
        requirePositive(quantity);
        this.onHand += quantity;
    }

    /** RETURN: stock coming back from a buyer. */
    public void registerReturn(int quantity) {
        receive(quantity);
    }

    /** RESERVATION: cannot reserve damaged or insufficient stock. */
    public void reserve(int quantity) {
        requirePositive(quantity);
        if (InventoryStatus.DAMAGED.equals(status)) {
            throw new DamagedInventoryException("Damaged inventory cannot be reserved.");
        }
        if (available() < quantity) {
            throw new InsufficientStockException("Not enough available stock: requested " + quantity
                    + ", available " + available() + ".");
        }
        this.reserved += quantity;
    }

    /** Gives back reserved stock (cancelled order). */
    public void releaseReservation(int quantity) {
        requirePositive(quantity);
        if (reserved < quantity) {
            throw new InvalidInventoryException("Cannot release more than the reserved quantity.");
        }
        this.reserved -= quantity;
    }

    /** SALE: reserved units leave the warehouse. */
    public void confirmSale(int quantity) {
        requirePositive(quantity);
        if (reserved < quantity || onHand < quantity) {
            throw new InvalidInventoryException("Cannot confirm a sale over unreserved stock.");
        }
        this.reserved -= quantity;
        this.onHand -= quantity;
    }

    /** ADJUSTMENT: sets the physical stock; it can never go below the reserved quantity or zero. */
    public void adjustTo(int newOnHand) {
        if (newOnHand < 0) {
            throw new InvalidInventoryException("Stock cannot be negative.");
        }
        if (newOnHand < reserved) {
            throw new InvalidInventoryException("Stock cannot be lower than the reserved quantity.");
        }
        this.onHand = newOnHand;
    }

    public void markDamaged() {
        if (InventoryStatus.DAMAGED.equals(status)) {
            throw new InvalidStatusTransitionException("Inventory", status, InventoryStatus.DAMAGED);
        }
        this.status = InventoryStatus.DAMAGED;
    }

    public void markAvailable() {
        if (InventoryStatus.AVAILABLE.equals(status)) {
            throw new InvalidStatusTransitionException("Inventory", status, InventoryStatus.AVAILABLE);
        }
        this.status = InventoryStatus.AVAILABLE;
    }

    public void ensureValid() {
        if (product == null || variant == null || warehouse == null) {
            throw new InvalidInventoryException("Inventory must be linked to a product, a variant and a warehouse.");
        }
        if (onHand < 0 || reserved < 0 || reserved > onHand) {
            throw new InvalidInventoryException("Inventory quantities are inconsistent.");
        }
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInventoryException("Quantity must be greater than zero.");
        }
    }

}
