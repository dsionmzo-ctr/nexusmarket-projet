package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.CartJpaMapper;
import application.adapters.persistence.jpa.repositories.CartJpaRepository;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.ports.out.CartRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the cart repository port. */
@Repository
@Transactional
public class CartJpaAdapter implements CartRepositoryPort {

    private final CartJpaRepository cartJpaRepository;

    public CartJpaAdapter(CartJpaRepository cartJpaRepository) {
        this.cartJpaRepository = cartJpaRepository;
    }

    @Override
    public Cart save(Cart cart) {
        return CartJpaMapper.toDomain(cartJpaRepository.save(CartJpaMapper.toEntity(cart)));
    }

    @Override
    public Optional<Cart> findByBuyer(Buyer buyer) {
        return cartJpaRepository.findByBuyer_Id(buyer.getId()).map(CartJpaMapper::toDomain);
    }
}
