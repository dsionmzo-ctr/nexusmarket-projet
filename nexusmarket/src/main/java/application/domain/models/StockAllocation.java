package application.domain.models;

import application.domain.exceptions.DomainException;

/**
 * Quantity of an order line reserved from one inventory record (one warehouse). An order line may
 * be served from several warehouses.
 */
public class StockAllocation {

    private Inventory inventory;
    private int quantity;

    public StockAllocation() {
    }

    public StockAllocation(Inventory inventory, int quantity) {
        if (inventory == null) {
            throw new DomainException("Stock allocation requires an inventory.");
        }
        if (quantity <= 0) {
            throw new DomainException("Stock allocation quantity must be greater than zero.");
        }
        this.inventory = inventory;
        this.quantity = quantity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
