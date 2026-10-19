package application.domain.models;

import application.domain.exceptions.InvalidProductException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Variation of a product (color, size, model...). Every product has at least one variant;
 * inventory and cart lines are expressed per variant.
 */
public class ProductVariant {

    private Long id;
    private String sku;
    private Map<String, String> attributes = new LinkedHashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }


    public void ensureValid() {
        if (sku == null || sku.isBlank()) {
            throw new InvalidProductException("Variant SKU must be provided.");
        }
    }

}
