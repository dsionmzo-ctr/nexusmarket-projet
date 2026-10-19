package application.adapters.rest.dtos.responses;

import java.util.Map;

/** Consolidated administrative information. */
public record AdministrativeSummaryResponseDTO(
        long totalUsers,
        Map<String, Long> usersByRole,
        Map<String, Long> ordersByStatus,
        MoneyResponseDTO paidSales,
        long publishedProducts,
        long pendingShipments,
        long pendingRefunds,
        long damagedInventoryRecords
) {
}
