package application.domain.ports.in;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.User;
import java.util.List;

/**
 * Input port: operations that do not require prior authentication, or that are needed for
 * onboarding and authentication (including the public catalog). All parameters are Domain Models.
 */
public interface PublicAccessPort {

    /** Authenticates the credentials carried by {@code user} (email + raw password) and issues the token. */
    AuthenticationResult login(User user);

    /** Closes the session: every token issued so far becomes invalid. */
    void logout(User user);

    /** Self-registration of a buyer (identity and primary address). */
    Buyer registerBuyer(Buyer buyer);

    /** Self-registration of a buyer together with its access account (raw password carried by {@code account}). */
    Buyer registerBuyerAccount(Buyer buyer, User account);

    /** Creates the system user (credentials) of an already registered buyer. */
    User registerBuyerUser(User user);

    /** Public catalog: only PUBLISHED products. */
    List<Product> consultPublishedProducts();

    Product consultPublishedProduct(Product product);
}
