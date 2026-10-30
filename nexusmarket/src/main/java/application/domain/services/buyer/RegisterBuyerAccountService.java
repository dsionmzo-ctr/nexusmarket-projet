package application.domain.services.buyer;

import application.domain.exceptions.InvalidUserException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.services.user.RegisterBuyerUserService;
import application.domain.services.user.ValidatePasswordPolicyService;

/**
 * Self-registration of a buyer together with its access account (credentials) in a single step, so that the
 * buyer can log in right after registering. The password policy is validated before anything is stored: a
 * weak password never leaves a buyer profile without an account.
 */
public class RegisterBuyerAccountService {

    private final ValidatePasswordPolicyService validatePasswordPolicyService;
    private final RegisterBuyerService registerBuyerService;
    private final RegisterBuyerUserService registerBuyerUserService;

    public RegisterBuyerAccountService(ValidatePasswordPolicyService validatePasswordPolicyService,
            RegisterBuyerService registerBuyerService,
            RegisterBuyerUserService registerBuyerUserService) {
        this.validatePasswordPolicyService = validatePasswordPolicyService;
        this.registerBuyerService = registerBuyerService;
        this.registerBuyerUserService = registerBuyerUserService;
    }

    public Buyer execute(Buyer buyer, User account) {
        if (account == null) {
            throw new InvalidUserException("The account credentials must be provided.");
        }
        validatePasswordPolicyService.execute(account.getRawPassword());
        Buyer saved = registerBuyerService.execute(buyer);
        account.setBuyer(saved);
        registerBuyerUserService.execute(account);
        return saved;
    }
}
