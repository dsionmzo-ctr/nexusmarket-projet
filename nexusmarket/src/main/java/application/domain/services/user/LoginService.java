package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserStatus;

/**
 * Authenticates a system user by e-mail and password and issues the access token. An unknown e-mail and
 * a wrong password produce the same error. Flow: search the user, validate the password, validate the
 * user status and generate the token through the output ports.
 */
public class LoginService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final JwtServicePort jwtServicePort;

    public LoginService(UserRepositoryPort userRepositoryPort,
            PasswordServicePort passwordServicePort,
            JwtServicePort jwtServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.jwtServicePort = jwtServicePort;
    }

    public AuthenticationResult execute(User user) {
        if (user == null || user.getEmail() == null || user.getRawPassword() == null) {
            throw new InvalidCredentialsException();
        }
        User stored = userRepositoryPort.findByEmail(user).orElseThrow(InvalidCredentialsException::new);
        stored.setRawPassword(user.getRawPassword());
        boolean matches = passwordServicePort.matches(stored);
        stored.setRawPassword(null);
        if (!matches) {
            throw new InvalidCredentialsException();
        }
        if (UserStatus.BLOCKED.equals(stored.getStatus())) {
            throw new UnauthorizedOperationException("User account is blocked.");
        }
        if (UserStatus.INACTIVE.equals(stored.getStatus())) {
            throw new UnauthorizedOperationException("User account is inactive.");
        }
        return new AuthenticationResult(stored, jwtServicePort.generateToken(stored), jwtServicePort.getExpirationSeconds());
    }
}
