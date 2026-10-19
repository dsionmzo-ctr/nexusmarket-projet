package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Relational representation of a buyer and its delivery addresses.
 */
@Entity
@Table(name = "buyers")
public class BuyerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String identification;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(nullable = false, unique = true, length = 160)
    private String email;
    @Column(nullable = false, length = 20)
    private String status;
    @Embedded
    private AddressEmbeddable primaryAddress;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "buyer_addresses", joinColumns = @JoinColumn(name = "buyer_id"))
    @Fetch(FetchMode.SUBSELECT)
    private List<AddressEmbeddable> additionalAddresses = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AddressEmbeddable getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(AddressEmbeddable primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public List<AddressEmbeddable> getAdditionalAddresses() {
        return additionalAddresses;
    }

    public void setAdditionalAddresses(List<AddressEmbeddable> additionalAddresses) {
        this.additionalAddresses = additionalAddresses;
    }

}
