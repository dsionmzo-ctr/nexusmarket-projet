package application.domain.models;

import application.domain.exceptions.InvalidCartException;

/**
 * Line of a shopping cart: a product variant and the desired quantity.
 */
public class CartItem {

    private Product product;
    private ProductVariant variant;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Product product, ProductVariant variant, int quantity) {
        if (product == null || variant == null) {
            throw new InvalidCartException("Cart item requires a product and a variant.");
        }
        if (quantity <= 0) {
            throw new InvalidCartException("Cart item quantity must be greater than zero.");
        }
        this.product = product;
        this.variant = variant;
        this.quantity = quantity;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void increaseBy(int delta) {
        changeQuantity(quantity + delta);
    }

    public void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidCartException("Cart item quantity must be greater than zero.");
        }
        this.quantity = newQuantity;
    }

}
