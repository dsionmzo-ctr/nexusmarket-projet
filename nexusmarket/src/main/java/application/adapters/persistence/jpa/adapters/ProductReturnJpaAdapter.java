package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.ProductReturnJpaMapper;
import application.adapters.persistence.jpa.repositories.ProductReturnJpaRepository;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.ProductReturn;
import application.domain.ports.out.ProductReturnRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the product return repository port. */
@Repository
@Transactional
public class ProductReturnJpaAdapter implements ProductReturnRepositoryPort {

    private final ProductReturnJpaRepository productReturnJpaRepository;

    public ProductReturnJpaAdapter(ProductReturnJpaRepository productReturnJpaRepository) {
        this.productReturnJpaRepository = productReturnJpaRepository;
    }

    @Override
    public ProductReturn save(ProductReturn productReturn) {
        return ProductReturnJpaMapper.toDomain(
                productReturnJpaRepository.save(ProductReturnJpaMapper.toEntity(productReturn)));
    }

    @Override
    public Optional<ProductReturn> findById(ProductReturn productReturn) {
        return productReturnJpaRepository.findById(productReturn.getId()).map(ProductReturnJpaMapper::toDomain);
    }

    @Override
    public List<ProductReturn> findByOrder(Order order) {
        return productReturnJpaRepository.findByOrder_Id(order.getId()).stream()
                .map(ProductReturnJpaMapper::toDomain).toList();
    }

    @Override
    public List<ProductReturn> findByBuyer(Buyer buyer) {
        return productReturnJpaRepository.findByOrder_Buyer_Id(buyer.getId()).stream()
                .map(ProductReturnJpaMapper::toDomain).toList();
    }

    /** A filter without status returns every return. */
    @Override
    public List<ProductReturn> findByStatus(ProductReturn productReturn) {
        if (productReturn.getStatus() == null) {
            return productReturnJpaRepository.findAll().stream().map(ProductReturnJpaMapper::toDomain).toList();
        }
        return productReturnJpaRepository.findByStatus(productReturn.getStatus().getCode()).stream()
                .map(ProductReturnJpaMapper::toDomain).toList();
    }

    @Override
    public void update(ProductReturn productReturn) {
        productReturnJpaRepository.save(ProductReturnJpaMapper.toEntity(productReturn));
    }
}
