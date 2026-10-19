package application.adapters.useCases;

import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.ports.in.AdminPort;
import application.domain.services.buyer.ChangeBuyerStatusService;
import application.domain.services.operation.ConsultAllOperationsService;
import application.domain.services.operation.ConsultAuditLogsService;
import application.domain.services.report.ConsultAdministrativeSummaryService;
import application.domain.services.returns.ApproveRefundService;
import application.domain.services.returns.ConsultRefundsService;
import application.domain.services.returns.RejectRefundService;
import application.domain.services.seller.IncorporateSellerService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUsersService;
import application.domain.services.user.RegisterSellerUserService;
import application.domain.services.user.RegisterStaffUserService;
import application.domain.services.warehouse.ChangeWarehouseStatusService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.services.warehouse.CreateWarehouseService;
import application.domain.services.warehouse.UpdateWarehouseService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case adapter of the Admin role: sellers, staff users, warehouses, refund decisions and audit.
 * Delegates every operation to the single-responsibility domain services; it holds no business rule.
 */
@Service
@Transactional
public class AdminUseCaseImpl implements AdminPort {

    private final IncorporateSellerService incorporateSellerService;
    private final RegisterSellerUserService registerSellerUserService;
    private final RegisterStaffUserService registerStaffUserService;
    private final ChangeUserStatusService changeUserStatusService;
    private final ConsultUsersService consultUsersService;
    private final ChangeBuyerStatusService changeBuyerStatusService;
    private final CreateWarehouseService createWarehouseService;
    private final UpdateWarehouseService updateWarehouseService;
    private final ChangeWarehouseStatusService changeWarehouseStatusService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultRefundsService consultRefundsService;
    private final ApproveRefundService approveRefundService;
    private final RejectRefundService rejectRefundService;
    private final ConsultAuditLogsService consultAuditLogsService;
    private final ConsultAllOperationsService consultAllOperationsService;
    private final ConsultAdministrativeSummaryService consultAdministrativeSummaryService;

    public AdminUseCaseImpl(IncorporateSellerService incorporateSellerService,
            RegisterSellerUserService registerSellerUserService,
            RegisterStaffUserService registerStaffUserService,
            ChangeUserStatusService changeUserStatusService,
            ConsultUsersService consultUsersService,
            ChangeBuyerStatusService changeBuyerStatusService,
            CreateWarehouseService createWarehouseService,
            UpdateWarehouseService updateWarehouseService,
            ChangeWarehouseStatusService changeWarehouseStatusService,
            ConsultWarehousesService consultWarehousesService,
            ConsultRefundsService consultRefundsService,
            ApproveRefundService approveRefundService,
            RejectRefundService rejectRefundService,
            ConsultAuditLogsService consultAuditLogsService,
            ConsultAllOperationsService consultAllOperationsService,
            ConsultAdministrativeSummaryService consultAdministrativeSummaryService) {
        this.incorporateSellerService = incorporateSellerService;
        this.registerSellerUserService = registerSellerUserService;
        this.registerStaffUserService = registerStaffUserService;
        this.changeUserStatusService = changeUserStatusService;
        this.consultUsersService = consultUsersService;
        this.changeBuyerStatusService = changeBuyerStatusService;
        this.createWarehouseService = createWarehouseService;
        this.updateWarehouseService = updateWarehouseService;
        this.changeWarehouseStatusService = changeWarehouseStatusService;
        this.consultWarehousesService = consultWarehousesService;
        this.consultRefundsService = consultRefundsService;
        this.approveRefundService = approveRefundService;
        this.rejectRefundService = rejectRefundService;
        this.consultAuditLogsService = consultAuditLogsService;
        this.consultAllOperationsService = consultAllOperationsService;
        this.consultAdministrativeSummaryService = consultAdministrativeSummaryService;
    }

    @Override
    public Seller registerSeller(User admin, Seller seller, Warehouse firstWarehouse) {
        return incorporateSellerService.execute(admin, seller, firstWarehouse);
    }

    @Override
    public User registerSellerUser(User admin, User sellerUser) {
        return registerSellerUserService.execute(admin, sellerUser);
    }

    @Override
    public User registerStaffUser(User admin, User newUser) {
        return registerStaffUserService.execute(admin, newUser);
    }

    @Override
    public User changeUserStatus(User admin, User target, UserStatus newStatus) {
        return changeUserStatusService.execute(admin, target, newStatus);
    }

    @Override
    public List<User> consultUsers(User admin) {
        return consultUsersService.execute(admin);
    }

    @Override
    public Buyer changeBuyerStatus(User admin, Buyer buyer, BuyerStatus newStatus) {
        return changeBuyerStatusService.execute(admin, buyer, newStatus);
    }

    @Override
    public Warehouse createWarehouse(User admin, Warehouse warehouse) {
        return createWarehouseService.execute(admin, warehouse);
    }

    @Override
    public Warehouse updateWarehouse(User admin, Warehouse changes) {
        return updateWarehouseService.execute(admin, changes);
    }

    @Override
    public Warehouse changeWarehouseStatus(User admin, Warehouse warehouse, WarehouseStatus newStatus) {
        return changeWarehouseStatusService.execute(admin, warehouse, newStatus);
    }

    @Override
    public List<Warehouse> consultWarehouses(User admin) {
        return consultWarehousesService.execute(admin);
    }

    @Override
    public List<Refund> consultPendingRefunds(User admin) {
        Refund filter = new Refund();
        filter.setStatus(RefundStatus.REQUESTED);
        return consultRefundsService.execute(admin, filter);
    }

    @Override
    public Refund approveRefund(User admin, Refund refund) {
        return approveRefundService.execute(admin, refund);
    }

    @Override
    public Refund rejectRefund(User admin, Refund refund) {
        return rejectRefundService.execute(admin, refund);
    }

    @Override
    public List<AuditLog> consultAuditLog(User admin, AuditLog filter) {
        return consultAuditLogsService.execute(admin, filter);
    }

    @Override
    public List<Operation> consultAllOperations(User admin) {
        return consultAllOperationsService.execute(admin);
    }

    @Override
    public AdministrativeSummary consultSummary(User admin) {
        return consultAdministrativeSummaryService.execute(admin);
    }
}
