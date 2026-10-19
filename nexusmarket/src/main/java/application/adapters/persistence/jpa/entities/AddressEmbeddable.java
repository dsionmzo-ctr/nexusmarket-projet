package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;

/**
 * Embeddable column group of an Address Value Object.
 */
@Embeddable
public class AddressEmbeddable {

    @Column(length = 200)
    private String street;
    @Column(length = 100)
    private String city;
    @Column(length = 100)
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
