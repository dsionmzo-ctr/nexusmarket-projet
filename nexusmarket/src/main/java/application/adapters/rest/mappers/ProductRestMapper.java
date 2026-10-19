package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.ProductVariantRequestDTO;
import application.adapters.rest.dtos.requests.RegisterProductRequestDTO;
import application.adapters.rest.dtos.requests.UpdateProductRequestDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.ProductVariantResponseDTO;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.valueobjects.ProductType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/** Product Domain Model (with variants) <-> product DTOs. */
public final class ProductRestMapper {

    private ProductRestMapper() {
    }

    public static Product toDomain(RegisterProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.name().trim());
        product.setDescription(dto.description());
        product.setType(ProductType.fromCode(dto.type()));
        product.setPrice(MoneyRestMapper.toDomain(dto.priceAmount(), dto.priceCurrency()));
        List<ProductVariant> variants = new ArrayList<>();
        dto.variants().forEach(v -> variants.add(toVariant(v)));
        product.setVariants(variants);
        return product;
    }

    public static ProductVariant toVariant(ProductVariantRequestDTO dto) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(dto.sku().trim());
        variant.setAttributes(dto.attributes() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(dto.attributes()));
        return variant;
    }

    public static Product toChanges(Long productId, UpdateProductRequestDTO dto) {
        Product product = new Product();
        product.setId(productId);
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(MoneyRestMapper.toDomain(dto.priceAmount(), dto.priceCurrency()));
        return product;
    }

    public static Product toProbe(Long productId) {
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    public static ProductResponseDTO toResponse(Product product) {
        List<ProductVariantResponseDTO> variants = product.getVariants().stream()
                .map(v -> new ProductVariantResponseDTO(v.getId(), v.getSku(), v.getAttributes())).toList();
        return new ProductResponseDTO(product.getId(),
                product.getSeller() == null ? null : product.getSeller().getId(),
                product.getSeller() == null ? null : product.getSeller().getBusinessName(),
                product.getName(), product.getDescription(), product.getType().getCode(),
                product.getStatus().getCode(), MoneyRestMapper.toResponse(product.getPrice()), variants);
    }
}
