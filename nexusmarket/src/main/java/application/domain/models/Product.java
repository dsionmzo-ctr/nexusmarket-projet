package application.domain.models;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.InvalidProductException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Catalog product offered by a seller. It can be physical (needs inventory and shipping) or
 * digital (delivered right after payment).
 *
 * New products are registered SUSPENDED (not visible) and become visible when published.
 * DISCONTINUED is terminal.
 */
public class Product extends MarketplaceResource {

    private Seller seller;
    private String name;
    private String description;
    private ProductType type;
    private ProductStatus status = ProductStatus.SUSPENDED;
    private Money price;
    private List<ProductVariant> variants = new ArrayList<>();

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }


    @Override
    public String getResourceType() {
        return "PRODUCT";
    }

    public void ensureValid() {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("Product name must be provided.");
        }
        if (type == null) {
            throw new InvalidProductException("Product type must be provided.");
        }
        if (price == null || !price.isPositive()) {
            throw new InvalidProductException("Product price must be greater than zero.");
        }
        if (variants.isEmpty()) {
            throw new InvalidProductException("A product requires at least one variant.");
        }
        variants.forEach(ProductVariant::ensureValid);
    }

    public void addVariant(ProductVariant variant) {
        requireNotDiscontinued();
        if (variant == null) {
            throw new InvalidProductException("Variant must be provided.");
        }
        variant.ensureValid();
        boolean duplicated = variants.stream().anyMatch(v -> v.getSku().equalsIgnoreCase(variant.getSku()));
        if (duplicated) {
            throw new DuplicateResourceException("Variant SKU already exists in the product: " + variant.getSku());
        }
        variants.add(variant);
    }

    public void update(String newName, String newDescription, Money newPrice) {
        requireNotDiscontinued();
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
        if (newPrice != null) {
            if (!newPrice.isPositive()) {
                throw new InvalidProductException("Product price must be greater than zero.");
            }
            this.price = newPrice;
        }
    }

    /** SUSPENDED -> PUBLISHED. */
    public void publish() {
        if (!ProductStatus.SUSPENDED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", status, ProductStatus.PUBLISHED);
        }
        this.status = ProductStatus.PUBLISHED;
    }

    /** PUBLISHED -> SUSPENDED. */
    public void suspend() {
        if (!ProductStatus.PUBLISHED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", status, ProductStatus.SUSPENDED);
        }
        this.status = ProductStatus.SUSPENDED;
    }

    /** PUBLISHED | SUSPENDED -> DISCONTINUED (terminal). */
    public void discontinue() {
        requireNotDiscontinued();
        this.status = ProductStatus.DISCONTINUED;
    }

    public boolean isPublished() {
        return ProductStatus.PUBLISHED.equals(status);
    }

    public boolean requiresInventory() {
        return ProductType.PHYSICAL.equals(type);
    }

    public boolean isOwnedBy(Seller candidate) {
        return seller != null && candidate != null && seller.getId() != null && seller.getId().equals(candidate.getId());
    }

    public Optional<ProductVariant> findVariant(Long variantId) {
        return variants.stream().filter(v -> v.getId() != null && v.getId().equals(variantId)).findFirst();
    }

    private void requireNotDiscontinued() {
        if (ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", status, "any other status");
        }
    }

}
