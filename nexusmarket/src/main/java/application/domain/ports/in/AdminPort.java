package application.domain.ports.in;

import application.domain.models.AdministrativeSummary;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.Operation;
import application.domain.models.Refund;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import java.util.List;

/**
 * Input port: operations of the ADMIN role (sellers, staff, buyers' commercial status, warehouses,
 * refunds, operations and audit).
 */
public interface AdminPort {

    /** Incorporates a seller together with his first warehouse. Sellers cannot self-register. */
    Seller registerSeller(User admin, Seller seller, Warehouse firstWarehouse);

    /** Creates the system user (credentials) of an already registered seller. */
    User registerSellerUser(User admin, User sellerUser);

    /** Registers a LOGISTIC_OPERATOR, SUPERVISOR or ADMIN user. */
    User registerStaffUser(User admin, User newUser);

    User changeUserStatus(User admin, User target, UserStatus newStatus);

    List<User> consultUsers(User admin);

    Buyer changeBuyerStatus(User admin, Buyer buyer, BuyerStatus newStatus);

    Warehouse createWarehouse(User admin, Warehouse warehouse);

    Warehouse updateWarehouse(User admin, Warehouse changes);

    Warehouse changeWarehouseStatus(User admin, Warehouse warehouse, WarehouseStatus newStatus);

    List<Warehouse> consultWarehouses(User admin);

    List<Refund> consultPendingRefunds(User admin);

    /** {@code refund} carries the refund id and the decision note. */
    Refund approveRefund(User admin, Refund refund);

    Refund rejectRefund(User admin, Refund refund);

    /** {@code filter} optionally carries the performing user and/or the operation type. */
    List<AuditLog> consultAuditLog(User admin, AuditLog filter);

    List<Operation> consultAllOperations(User admin);

    AdministrativeSummary consultSummary(User admin);
}
