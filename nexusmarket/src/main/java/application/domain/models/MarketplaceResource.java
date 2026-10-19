package application.domain.models;

/**
 * Abstract base of every marketplace resource that can be the subject of an {@link Operation}
 * (the equivalent of the abstract product of the reference domain). Every significant business
 * action performed on a resource must generate an Operation, which is recorded in the AuditLog.
 */
public abstract class MarketplaceResource {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    /** Stable name of the kind of resource, used by the audit trail (e.g. "ORDER"). */
    public abstract String getResourceType();

}
