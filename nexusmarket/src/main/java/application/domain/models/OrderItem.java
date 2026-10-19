package application.domain.models;

import application.domain.exceptions.InvalidOrderException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;

/**
 * Line of an order. Product data is copied (name, SKU, price) so the order is not affected by
 * later catalog changes. Physical lines remember from which inventory records their units were
 * reserved.
 */
public class OrderItem {

    private Long id;
    private Product product;
    private ProductVariant variant;
    private Seller seller;
    private String productName;
    private String sku;
    private ProductType productType;
    private Money unitPrice;
    private int quantity;
    private List<StockAllocation> allocations = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<StockAllocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<StockAllocation> allocations) {
        this.allocations = allocations;
    }


    public Money lineTotal() {
        return unitPrice.multiply(quantity);
    }

    public boolean requiresShipping() {
        return ProductType.PHYSICAL.equals(productType);
    }

    public void ensureValid() {
        if (product == null || variant == null || seller == null) {
            throw new InvalidOrderException("Order item requires product, variant and seller.");
        }
        if (unitPrice == null || quantity <= 0) {
            throw new InvalidOrderException("Order item requires a price and a positive quantity.");
        }
    }

}
