package application.domain.models;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidUserException;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import java.util.ArrayList;
import java.util.List;

/**
 * Commercial profile of a person who buys: delivery addresses and the commercial status that
 * allows (or not) placing purchases. A buyer never manages other buyers' information.
 */
public class Buyer extends Person {

    private BuyerStatus status = BuyerStatus.ENABLED;
    private Address primaryAddress;
    private List<Address> additionalAddresses = new ArrayList<>();

    public Buyer() {
        setRole(SystemRole.BUYER);
    }

    public BuyerStatus getStatus() {
        return status;
    }

    public void setStatus(BuyerStatus status) {
        this.status = status;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public List<Address> getAdditionalAddresses() {
        return additionalAddresses;
    }

    public void setAdditionalAddresses(List<Address> additionalAddresses) {
        this.additionalAddresses = additionalAddresses;
    }


    @Override
    public String getResourceType() {
        return "BUYER";
    }

    public void ensureComplete() {
        ensureIdentityValid();
        if (primaryAddress == null) {
            throw new InvalidUserException("Buyer primary address must be provided.");
        }
    }

    public void addAddress(Address address) {
        if (address == null) {
            throw new InvalidUserException("Address must be provided.");
        }
        if (ownsAddress(address)) {
            throw new DomainException("The address is already registered for this buyer.");
        }
        additionalAddresses.add(address);
    }

    public void removeAddress(Address address) {
        if (address != null && address.equals(primaryAddress)) {
            throw new DomainException("The primary address cannot be removed.");
        }
        if (!additionalAddresses.remove(address)) {
            throw new DomainException("The address is not registered for this buyer.");
        }
    }

    public boolean ownsAddress(Address address) {
        return address != null && (address.equals(primaryAddress) || additionalAddresses.contains(address));
    }

    public boolean canPurchase() {
        return BuyerStatus.ENABLED.equals(status);
    }

    public void requireCanPurchase() {
        if (!canPurchase()) {
            throw new DomainException("The buyer is not commercially enabled to purchase.");
        }
    }

    public void changeStatus(BuyerStatus target) {
        if (target == null || target.equals(status)) {
            throw new InvalidStatusTransitionException("Buyer", status, target);
        }
        this.status = target;
    }

    public boolean sameBuyerAs(Buyer other) {
        return other != null && getId() != null && getId().equals(other.getId());
    }

}
