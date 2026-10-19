package application.domain.models;

/**
 * Lightweight reference to a marketplace resource identified by its type and identifier. Persistence
 * adapters use it to rebuild the affected resource of an Operation or AuditLog without loading it.
 */
public class ResourceReference extends MarketplaceResource {

    private String resourceType;

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }


    public ResourceReference() {
    }

    public ResourceReference(String resourceType, Long id) {
        this.resourceType = resourceType;
        setId(id);
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

}
