package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Changes the access status of a user (ACTIVE, INACTIVE, BLOCKED). Only an ADMIN can do it. The token
 * version is bumped, so the sessions of the affected user are closed.
 */
public class ChangeUserStatusService {

    private final AuthorizeOperationService authorizeOperationService;
    private final UserRepositoryPort userRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ChangeUserStatusService(AuthorizeOperationService authorizeOperationService,
            UserRepositoryPort userRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.userRepositoryPort = userRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public User execute(User admin, User target, UserStatus newStatus) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        User stored = userRepositoryPort.findById(target)
                .orElseThrow(() -> new EntityNotFoundException("User", target.getId()));
        if (stored.getId().equals(admin.getId()) && !UserStatus.ACTIVE.equals(newStatus)) {
            throw new InvalidUserException("An administrator cannot disable his own account.");
        }
        UserStatus previous = stored.getStatus();
        stored.changeStatus(newStatus);
        userRepositoryPort.update(stored);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.USER_STATUS_CHANGE, admin, stored, LocalDateTime.now(clock)),
                AuditDetails.of("previousStatus", previous.getCode(), "newStatus", newStatus.getCode()));
        return stored;
    }
}
