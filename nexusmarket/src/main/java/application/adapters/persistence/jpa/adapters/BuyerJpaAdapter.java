package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.repositories.BuyerJpaRepository;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the buyer repository port. */
@Repository
@Transactional
public class BuyerJpaAdapter implements BuyerRepositoryPort {

    private final BuyerJpaRepository buyerJpaRepository;

    public BuyerJpaAdapter(BuyerJpaRepository buyerJpaRepository) {
        this.buyerJpaRepository = buyerJpaRepository;
    }

    @Override
    public Buyer save(Buyer buyer) {
        return BuyerJpaMapper.toDomain(buyerJpaRepository.save(BuyerJpaMapper.toEntity(buyer)));
    }

    @Override
    public Optional<Buyer> findById(Buyer buyer) {
        return buyerJpaRepository.findById(buyer.getId()).map(BuyerJpaMapper::toDomain);
    }

    @Override
    public Optional<Buyer> findByIdentification(Buyer buyer) {
        return buyerJpaRepository.findByIdentification(buyer.getIdentification()).map(BuyerJpaMapper::toDomain);
    }

    @Override
    public boolean existsByIdentification(Buyer buyer) {
        return buyerJpaRepository.existsByIdentification(buyer.getIdentification());
    }

    @Override
    public boolean existsByEmail(Buyer buyer) {
        return buyerJpaRepository.existsByEmail(buyer.getEmail());
    }

    @Override
    public List<Buyer> findAll() {
        return buyerJpaRepository.findAll().stream().map(BuyerJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Buyer buyer) {
        buyerJpaRepository.save(BuyerJpaMapper.toEntity(buyer));
    }
}
