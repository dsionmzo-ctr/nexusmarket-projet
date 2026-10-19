package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.InventoryAdjustmentRequestDTO;
import application.adapters.rest.dtos.requests.InventoryInflowRequestDTO;
import application.adapters.rest.dtos.requests.InventoryNoteRequestDTO;
import application.adapters.rest.dtos.responses.InventoryMovementResponseDTO;
import application.adapters.rest.dtos.responses.InventoryResponseDTO;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.ProductVariant;
import application.domain.models.Warehouse;

/** Inventory and InventoryMovement Domain Models <-> inventory DTOs. */
public final class InventoryRestMapper {

    private InventoryRestMapper() {
    }

    public static InventoryMovement toInflow(InventoryInflowRequestDTO dto) {
        Inventory inventory = new Inventory();
        ProductVariant variant = new ProductVariant();
        variant.setId(dto.variantId());
        inventory.setVariant(variant);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.warehouseId());
        inventory.setWarehouse(warehouse);
        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(inventory);
        movement.setQuantity(dto.quantity());
        movement.setNote(dto.note());
        return movement;
    }

    public static InventoryMovement toAdjustment(Long inventoryId, InventoryAdjustmentRequestDTO dto) {
        InventoryMovement movement = toNote(inventoryId, null);
        movement.setQuantity(dto.newQuantity());
        movement.setNote(dto.note());
        return movement;
    }

    /** Movement that only identifies an inventory record and carries an optional note. */
    public static InventoryMovement toNote(Long inventoryId, InventoryNoteRequestDTO dto) {
        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(toProbe(inventoryId));
        movement.setNote(dto == null ? null : dto.note());
        return movement;
    }

    public static Inventory toProbe(Long inventoryId) {
        Inventory inventory = new Inventory();
        inventory.setId(inventoryId);
        return inventory;
    }

    public static InventoryResponseDTO toResponse(Inventory inventory) {
        return new InventoryResponseDTO(inventory.getId(), inventory.getProduct().getId(),
                inventory.getProduct().getName(), inventory.getVariant().getId(), inventory.getVariant().getSku(),
                inventory.getWarehouse().getId(), inventory.getWarehouse().getName(), inventory.getOnHand(),
                inventory.getReserved(), inventory.available(), inventory.getStatus().getCode());
    }

    public static InventoryMovementResponseDTO toResponse(InventoryMovement movement) {
        return new InventoryMovementResponseDTO(movement.getId(),
                movement.getInventory() == null ? null : movement.getInventory().getId(),
                movement.getType().getCode(), movement.getQuantity(), movement.getOnHandAfter(),
                movement.getPerformedBy() == null ? null : movement.getPerformedBy().getId(),
                movement.getOccurredAt(), movement.getNote());
    }
}
