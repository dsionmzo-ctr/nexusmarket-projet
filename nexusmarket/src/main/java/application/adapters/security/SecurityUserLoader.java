package application.adapters.security;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.adapters.security.mappers.JwtClaimsMapper;
import application.domain.exceptions.DomainException;
import application.domain.models.User;
import application.domain.services.user.LoadAuthenticatedUserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

/**
 * Security adapter towards the Domain: parses the token subject, delegates the authoritative load
 * (existence, active status and token version) to {@link LoadAuthenticatedUserService}, wraps the
 * result in the Spring principal and translates domain failures into authentication failures.
 */
@Component
public class SecurityUserLoader {

    private final LoadAuthenticatedUserService loadAuthenticatedUserService;

    public SecurityUserLoader(LoadAuthenticatedUserService loadAuthenticatedUserService) {
        this.loadAuthenticatedUserService = loadAuthenticatedUserService;
    }

    public AuthenticatedUserPrincipal loadActiveUser(String subject, long tokenVersion) {
        Long userId = JwtClaimsMapper.extractUserId(subject);
        if (userId == null) {
            throw new BadCredentialsException("Invalid token subject.");
        }
        try {
            User stored = loadAuthenticatedUserService.execute(userId, tokenVersion);
            return new AuthenticatedUserPrincipal(stored);
        } catch (DomainException ex) {
            throw new BadCredentialsException("The token does not match an active user.", ex);
        }
    }
}
