package application.adapters.useCases;

import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.buyer.RegisterBuyerService;
import application.domain.services.catalog.ConsultPublishedProductService;
import application.domain.services.catalog.ConsultPublishedProductsService;
import application.domain.services.user.LoginService;
import application.domain.services.user.LogoutService;
import application.domain.services.user.RegisterBuyerUserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case adapter of the public (unauthenticated) access port: login, buyer registration and public catalog.
 * Delegates every operation to the single-responsibility domain services; it holds no business rule.
 */
@Service
@Transactional
public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterBuyerService registerBuyerService;
    private final RegisterBuyerUserService registerBuyerUserService;
    private final ConsultPublishedProductsService consultPublishedProductsService;
    private final ConsultPublishedProductService consultPublishedProductService;

    public PublicAccessUseCaseImpl(LoginService loginService,
            LogoutService logoutService,
            RegisterBuyerService registerBuyerService,
            RegisterBuyerUserService registerBuyerUserService,
            ConsultPublishedProductsService consultPublishedProductsService,
            ConsultPublishedProductService consultPublishedProductService) {
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.registerBuyerService = registerBuyerService;
        this.registerBuyerUserService = registerBuyerUserService;
        this.consultPublishedProductsService = consultPublishedProductsService;
        this.consultPublishedProductService = consultPublishedProductService;
    }

    @Override
    public AuthenticationResult login(User user) {
        return loginService.execute(user);
    }

    @Override
    public void logout(User user) {
        // Idempotent: an anonymous or unknown caller resolves to a no-op.
        if (user == null || user.getId() == null) {
            return;
        }
        logoutService.execute(user);
    }

    @Override
    public Buyer registerBuyer(Buyer buyer) {
        return registerBuyerService.execute(buyer);
    }

    @Override
    public User registerBuyerUser(User user) {
        return registerBuyerUserService.execute(user);
    }

    @Override
    public List<Product> consultPublishedProducts() {
        return consultPublishedProductsService.execute();
    }

    @Override
    public Product consultPublishedProduct(Product product) {
        return consultPublishedProductService.execute(product);
    }
}
