package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.domain.enums.AuditSeverity;
import application.domain.models.AuditLog;
import application.domain.models.ResourceReference;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** AuditLog Domain Model <-> AuditLogDocument. */
public final class AuditLogMongoMapper {

    private AuditLogMongoMapper() {
    }

    public static AuditLogDocument toDocument(AuditLog domain) {
        AuditLogDocument document = new AuditLogDocument();
        document.setId(domain.getAuditId());
        document.setOperationType(domain.getOperationType().getCode());
        document.setOperationDate(domain.getOperationDate());
        document.setPerformedById(domain.getPerformedBy().getId());
        document.setPerformedByEmail(domain.getPerformedBy().getEmail());
        document.setUserRole(domain.getUserRole().getCode());
        document.setResourceType(domain.getAffectedResource().getResourceType());
        document.setResourceId(domain.getAffectedResource().getId());
        document.setDetails(sanitize(domain.getDetails()));
        document.setSeverity(domain.getSeverity().name());
        return document;
    }

    public static AuditLog toDomain(AuditLogDocument document) {
        AuditLog domain = new AuditLog();
        domain.setAuditId(document.getId());
        domain.setOperationType(OperationType.fromCode(document.getOperationType()));
        domain.setOperationDate(document.getOperationDate());
        User performer = new User();
        performer.setId(document.getPerformedById());
        performer.setEmail(document.getPerformedByEmail());
        domain.setPerformedBy(performer);
        domain.setUserRole(SystemRole.fromCode(document.getUserRole()));
        domain.setAffectedResource(new ResourceReference(document.getResourceType(), document.getResourceId()));
        domain.setDetails(new LinkedHashMap<>(document.getDetails()));
        domain.setSeverity(AuditSeverity.valueOf(document.getSeverity()));
        return domain;
    }

    /** Keeps the stored details to simple JSON-friendly values (no BigDecimal / domain objects). */
    private static Map<String, Object> sanitize(Map<String, Object> details) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (details == null) {
            return result;
        }
        details.forEach((key, value) -> {
            if (value == null) {
                result.put(key, null);
            } else if (value instanceof BigDecimal decimal) {
                result.put(key, decimal.toPlainString());
            } else if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                result.put(key, value);
            } else {
                result.put(key, value.toString());
            }
        });
        return result;
    }
}
