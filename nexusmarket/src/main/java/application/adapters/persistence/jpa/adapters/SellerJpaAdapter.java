package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.repositories.SellerJpaRepository;
import application.domain.models.Seller;
import application.domain.ports.out.SellerRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the seller repository port. */
@Repository
@Transactional
public class SellerJpaAdapter implements SellerRepositoryPort {

    private final SellerJpaRepository sellerJpaRepository;

    public SellerJpaAdapter(SellerJpaRepository sellerJpaRepository) {
        this.sellerJpaRepository = sellerJpaRepository;
    }

    @Override
    public Seller save(Seller seller) {
        return SellerJpaMapper.toDomain(sellerJpaRepository.save(SellerJpaMapper.toEntity(seller)));
    }

    @Override
    public Optional<Seller> findById(Seller seller) {
        return sellerJpaRepository.findById(seller.getId()).map(SellerJpaMapper::toDomain);
    }

    @Override
    public Optional<Seller> findByIdentification(Seller seller) {
        return sellerJpaRepository.findByIdentification(seller.getIdentification()).map(SellerJpaMapper::toDomain);
    }

    @Override
    public boolean existsByIdentification(Seller seller) {
        return sellerJpaRepository.existsByIdentification(seller.getIdentification());
    }

    @Override
    public boolean existsByEmail(Seller seller) {
        return sellerJpaRepository.existsByEmail(seller.getEmail());
    }

    @Override
    public List<Seller> findAll() {
        return sellerJpaRepository.findAll().stream().map(SellerJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Seller seller) {
        sellerJpaRepository.save(SellerJpaMapper.toEntity(seller));
    }
}
