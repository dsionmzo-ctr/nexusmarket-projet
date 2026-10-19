package application.domain.ports.out;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceResource;
import application.domain.models.User;
import java.util.List;

/**
 * Output port: persistence of immutable audit records. The implementation is expected to use
 * MongoDB while the Domain remains completely unaware of it.
 */
public interface AuditLogRepositoryPort {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUser(User user);

    List<AuditLog> findByProduct(MarketplaceResource resource);

    List<AuditLog> findByOperationType(AuditLog auditLog);

    List<AuditLog> findAll();
}
