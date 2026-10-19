package application.domain.ports.out;

import application.domain.models.User;

/**
 * Output port: generation of the access token. The Domain must not depend on JWT libraries.
 * The token must never contain the user's password.
 */
public interface JwtServicePort {

    String generateToken(User user);

    /** Lifetime, in seconds, of the tokens issued by this service. */
    long getExpirationSeconds();
}
