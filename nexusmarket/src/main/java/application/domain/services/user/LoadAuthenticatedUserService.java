package application.domain.services.user;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Loads the current authoritative user of a token (subject = immutable user id). It rejects unknown,
 * inactive or blocked users and tokens whose version differs from the persisted authTokenVersion.
 * The role and the buyer/seller association come from the loaded user, never from token claims.
 */
public class LoadAuthenticatedUserService {

    private final UserRepositoryPort userRepositoryPort;

    public LoadAuthenticatedUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User execute(Long userId, long tokenVersion) {
        if (userId == null) {
            throw new UnauthorizedOperationException("Authenticated user id must be provided.");
        }
        User probe = new User();
        probe.setId(userId);
        User stored = userRepositoryPort.findById(probe)
                .orElseThrow(() -> new UnauthorizedOperationException("Authenticated user does not exist."));
        if (!stored.isActive()) {
            throw new UnauthorizedOperationException("Authenticated user is not active.");
        }
        if (stored.getAuthTokenVersion() != tokenVersion) {
            throw new UnauthorizedOperationException("The token is no longer valid.");
        }
        return stored;
    }
}
