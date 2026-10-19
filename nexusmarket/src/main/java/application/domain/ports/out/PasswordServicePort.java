package application.domain.ports.out;

import application.domain.models.User;

/**
 * Output port: password hashing and verification, abstracted from any concrete algorithm.
 */
public interface PasswordServicePort {

    /** Compares the raw password carried by the user with its stored hash. */
    boolean matches(User user);

    /** Hashes the raw password carried by the user. */
    String encrypt(User user);
}
