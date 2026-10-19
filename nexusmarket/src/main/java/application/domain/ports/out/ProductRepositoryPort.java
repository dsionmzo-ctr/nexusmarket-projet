package application.domain.ports.out;

import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of catalog products and their variants.
 */
public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(Product product);

    /** Resolves the product that owns a variant. */
    Optional<Product> findByVariant(ProductVariant variant);

    List<Product> findAll();

    /** Products with the status of the supplied model (e.g. PUBLISHED for the public catalog). */
    List<Product> findByStatus(Product product);

    List<Product> findBySeller(Seller seller);

    boolean existsVariantSku(ProductVariant variant);

    void update(Product product);
}
