package application.domain.models;

import application.domain.exceptions.InvalidUserException;
import application.domain.valueobjects.SystemRole;

/**
 * Commercial profile of a seller. Sellers cannot self-register: they are incorporated by the
 * Administrator together with their first warehouse.
 */
public class Seller extends Person {

    private String businessName;

    public Seller() {
        setRole(SystemRole.SELLER);
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }


    @Override
    public String getResourceType() {
        return "SELLER";
    }

    public void ensureComplete() {
        ensureIdentityValid();
        if (businessName == null || businessName.isBlank()) {
            throw new InvalidUserException("Seller business name must be provided.");
        }
    }

    public boolean sameSellerAs(Seller other) {
        return other != null && getId() != null && getId().equals(other.getId());
    }

}
