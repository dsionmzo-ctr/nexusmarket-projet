package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;

/**
 * Relational representation of a system user (Repository DTO; never exposed outside the persistence adapter).
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String identification;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(nullable = false, unique = true, length = 160)
    private String email;
    @Column(nullable = false, length = 30)
    private String role;
    @Column(nullable = false, length = 120)
    private String passwordHash;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private long authTokenVersion = 1L;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = true)
    private BuyerJpaEntity buyer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = true)
    private SellerJpaEntity seller;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAuthTokenVersion() {
        return authTokenVersion;
    }

    public void setAuthTokenVersion(long authTokenVersion) {
        this.authTokenVersion = authTokenVersion;
    }

    public BuyerJpaEntity getBuyer() {
        return buyer;
    }

    public void setBuyer(BuyerJpaEntity buyer) {
        this.buyer = buyer;
    }

    public SellerJpaEntity getSeller() {
        return seller;
    }

    public void setSeller(SellerJpaEntity seller) {
        this.seller = seller;
    }

}
