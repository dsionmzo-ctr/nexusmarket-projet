package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.entities.ProductVariantJpaEntity;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/** Product Domain Model (with its variants) <-> ProductJpaEntity / ProductVariantJpaEntity. */
public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductJpaEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(domain.getId());
        entity.setSeller(SellerJpaMapper.ref(domain.getSeller()));
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType().getCode());
        entity.setStatus(domain.getStatus().getCode());
        entity.setPriceAmount(domain.getPrice().amount());
        entity.setPriceCurrency(domain.getPrice().currency().getCode());
        List<ProductVariantJpaEntity> variants = new ArrayList<>();
        for (ProductVariant variant : domain.getVariants()) {
            ProductVariantJpaEntity variantEntity = new ProductVariantJpaEntity();
            variantEntity.setId(variant.getId());
            variantEntity.setProduct(entity);
            variantEntity.setSku(variant.getSku());
            variantEntity.setAttributes(new LinkedHashMap<>(variant.getAttributes()));
            variants.add(variantEntity);
        }
        entity.setVariants(variants);
        return entity;
    }

    /** Full product, with seller and variants. */
    public static Product toDomain(ProductJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Product domain = toShallowDomain(entity);
        List<ProductVariant> variants = new ArrayList<>();
        entity.getVariants().forEach(v -> variants.add(variantToDomain(v)));
        domain.setVariants(variants);
        return domain;
    }

    /** Product without variants (enough for order lines and reports). */
    public static Product toShallowDomain(ProductJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Product domain = new Product();
        domain.setId(entity.getId());
        domain.setSeller(SellerJpaMapper.toDomain(entity.getSeller()));
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setType(ProductType.fromCode(entity.getType()));
        domain.setStatus(ProductStatus.fromCode(entity.getStatus()));
        domain.setPrice(MoneyJpaMapper.toDomain(entity.getPriceAmount(), entity.getPriceCurrency()));
        return domain;
    }

    public static ProductVariant variantToDomain(ProductVariantJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductVariant variant = new ProductVariant();
        variant.setId(entity.getId());
        variant.setSku(entity.getSku());
        variant.setAttributes(new LinkedHashMap<>(entity.getAttributes()));
        return variant;
    }

    public static ProductJpaEntity ref(Product domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }

    public static ProductVariantJpaEntity variantRef(ProductVariant domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        ProductVariantJpaEntity entity = new ProductVariantJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
