package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.RefundJpaMapper;
import application.adapters.persistence.jpa.repositories.RefundJpaRepository;
import application.domain.models.Buyer;
import application.domain.models.ProductReturn;
import application.domain.models.Refund;
import application.domain.ports.out.RefundRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the refund repository port. */
@Repository
@Transactional
public class RefundJpaAdapter implements RefundRepositoryPort {

    private final RefundJpaRepository refundJpaRepository;

    public RefundJpaAdapter(RefundJpaRepository refundJpaRepository) {
        this.refundJpaRepository = refundJpaRepository;
    }

    @Override
    public Refund save(Refund refund) {
        return RefundJpaMapper.toDomain(refundJpaRepository.save(RefundJpaMapper.toEntity(refund)));
    }

    @Override
    public Optional<Refund> findById(Refund refund) {
        return refundJpaRepository.findById(refund.getId()).map(RefundJpaMapper::toDomain);
    }

    @Override
    public Optional<Refund> findByReturn(ProductReturn productReturn) {
        return refundJpaRepository.findByProductReturn_Id(productReturn.getId()).map(RefundJpaMapper::toDomain);
    }

    @Override
    public List<Refund> findByBuyer(Buyer buyer) {
        return refundJpaRepository.findByProductReturn_Order_Buyer_Id(buyer.getId()).stream()
                .map(RefundJpaMapper::toDomain).toList();
    }

    /** A filter without status returns every refund. */
    @Override
    public List<Refund> findByStatus(Refund refund) {
        if (refund.getStatus() == null) {
            return refundJpaRepository.findAll().stream().map(RefundJpaMapper::toDomain).toList();
        }
        return refundJpaRepository.findByStatus(refund.getStatus().getCode()).stream()
                .map(RefundJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Refund refund) {
        refundJpaRepository.save(RefundJpaMapper.toEntity(refund));
    }
}
