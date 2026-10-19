package application.domain.services.user;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the users of the platform (ADMIN and SUPERVISOR).
 */
public class ConsultUsersService {

    private final AuthorizeOperationService authorizeOperationService;
    private final UserRepositoryPort userRepositoryPort;

    public ConsultUsersService(AuthorizeOperationService authorizeOperationService,
            UserRepositoryPort userRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.userRepositoryPort = userRepositoryPort;
    }

    public List<User> execute(User requester) {
        authorizeOperationService.execute(requester, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return userRepositoryPort.findAll();
    }
}
