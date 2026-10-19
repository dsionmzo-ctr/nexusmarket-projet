package application.domain.models;

import application.domain.exceptions.InvalidCartException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provisional selection of products of a buyer (the "Cart" stage of the purchase). A buyer has
 * one cart; confirming it creates an Order and empties it.
 */
public class Cart {

    private Long id;
    private Buyer buyer;
    private List<CartItem> items = new ArrayList<>();
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    /** Adds a variant; if it is already in the cart the quantities are added. */
    public void addItem(Product product, ProductVariant variant, int quantity, LocalDateTime now) {
        Optional<CartItem> existing = findItem(variant.getId());
        if (existing.isPresent()) {
            existing.get().increaseBy(quantity);
        } else {
            items.add(new CartItem(product, variant, quantity));
        }
        this.updatedAt = now;
    }

    public void updateQuantity(Long variantId, int quantity, LocalDateTime now) {
        findItem(variantId)
                .orElseThrow(() -> new InvalidCartException("The variant is not in the cart."))
                .changeQuantity(quantity);
        this.updatedAt = now;
    }

    public void removeItem(Long variantId, LocalDateTime now) {
        boolean removed = items.removeIf(i -> i.getVariant().getId().equals(variantId));
        if (!removed) {
            throw new InvalidCartException("The variant is not in the cart.");
        }
        this.updatedAt = now;
    }

    public void clear(LocalDateTime now) {
        items.clear();
        this.updatedAt = now;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Optional<CartItem> findItem(Long variantId) {
        return items.stream().filter(i -> i.getVariant().getId().equals(variantId)).findFirst();
    }

}
