package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Closes the session of a user: its authTokenVersion is incremented so every previously issued token is
 * rejected from then on.
 */
public class LogoutService {

    private final UserRepositoryPort userRepositoryPort;

    public LogoutService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public void execute(User user) {
        if (user == null || user.getId() == null) {
            throw new UnauthorizedOperationException("Authenticated user must be provided for logout.");
        }
        userRepositoryPort.findById(user).orElseThrow(() -> new EntityNotFoundException("User", user.getId()));
        userRepositoryPort.incrementAuthTokenVersion(user);
    }
}
