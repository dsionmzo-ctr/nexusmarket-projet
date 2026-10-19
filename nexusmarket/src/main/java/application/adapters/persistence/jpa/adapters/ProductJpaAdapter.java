package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.persistence.jpa.repositories.ProductJpaRepository;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.ports.out.ProductRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the product repository port (product + variants aggregate). */
@Repository
@Transactional
public class ProductJpaAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;

    public ProductJpaAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product save(Product product) {
        return ProductJpaMapper.toDomain(productJpaRepository.save(ProductJpaMapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(Product product) {
        return productJpaRepository.findById(product.getId()).map(ProductJpaMapper::toDomain);
    }

    @Override
    public Optional<Product> findByVariant(ProductVariant variant) {
        return productJpaRepository.findByVariants_Id(variant.getId()).map(ProductJpaMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public List<Product> findByStatus(Product product) {
        return productJpaRepository.findByStatus(product.getStatus().getCode()).stream()
                .map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public List<Product> findBySeller(Seller seller) {
        return productJpaRepository.findBySeller_Id(seller.getId()).stream().map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public boolean existsVariantSku(ProductVariant variant) {
        return productJpaRepository.existsByVariants_Sku(variant.getSku());
    }

    @Override
    public void update(Product product) {
        productJpaRepository.save(ProductJpaMapper.toEntity(product));
    }
}
