package application.domain.models;

import application.domain.exceptions.InvalidUserException;
import application.domain.valueobjects.SystemRole;
import java.util.regex.Pattern;

/**
 * Abstract identifiable person of the marketplace. Centralizes the identity shared by buyers,
 * sellers and users. The role represents what the person means within the system.
 */
public abstract class Person extends MarketplaceResource {

    private String identification;
    private String name;
    private String email;
    private SystemRole role;

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

    public SystemRole getRole() {
        return role;
    }

    public void setRole(SystemRole role) {
        this.role = role;
    }


    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** The e-mail is stored trimmed and in lower case so uniqueness is case-insensitive. */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }

    /** Guarantees the mandatory identity attributes (identification, name, e-mail format, role). */
    public void ensureIdentityValid() {
        if (identification == null || identification.isBlank()) {
            throw new InvalidUserException("Identification must be provided.");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidUserException("Name must be provided.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidUserException("A valid e-mail must be provided.");
        }
        if (role == null) {
            throw new InvalidUserException("Role must be provided.");
        }
    }

}
