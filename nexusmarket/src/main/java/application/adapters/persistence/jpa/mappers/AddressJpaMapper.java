package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.AddressEmbeddable;
import application.domain.valueobjects.Address;

/** Address Value Object <-> embeddable column group. */
public final class AddressJpaMapper {

    private AddressJpaMapper() {
    }

    public static AddressEmbeddable toEmbeddable(Address address) {
        if (address == null) {
            return null;
        }
        AddressEmbeddable embeddable = new AddressEmbeddable();
        embeddable.setStreet(address.street());
        embeddable.setCity(address.city());
        embeddable.setCountry(address.country());
        return embeddable;
    }

    public static Address toDomain(AddressEmbeddable embeddable) {
        if (embeddable == null || embeddable.getStreet() == null) {
            return null;
        }
        return new Address(embeddable.getStreet(), embeddable.getCity(), embeddable.getCountry());
    }
}
