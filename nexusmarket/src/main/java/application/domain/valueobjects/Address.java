package application.domain.valueobjects;

import application.domain.exceptions.DomainException;

/**
 * Value Object: a delivery or storage location.
 * All parts are mandatory; two addresses with the same parts are the same address.
 */
public record Address(String street, String city, String country) {

    public Address {
        street = requireText(street, "street");
        city = requireText(city, "city");
        country = requireText(country, "country");
    }

    public static Address of(String street, String city, String country) {
        return new Address(street, city, country);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Address " + field + " must be provided.");
        }
        return value.trim();
    }
}
