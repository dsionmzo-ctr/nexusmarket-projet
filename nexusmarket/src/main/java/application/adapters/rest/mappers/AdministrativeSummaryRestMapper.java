package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.AdministrativeSummaryResponseDTO;
import application.domain.models.AdministrativeSummary;

import java.util.LinkedHashMap;

/** AdministrativeSummary Domain Model -> summary DTO. */
public final class AdministrativeSummaryRestMapper {

    private AdministrativeSummaryRestMapper() {
    }

    public static AdministrativeSummaryResponseDTO toResponse(AdministrativeSummary summary) {
        return new AdministrativeSummaryResponseDTO(summary.getTotalUsers(),
                new LinkedHashMap<>(summary.getUsersByRole()), new LinkedHashMap<>(summary.getOrdersByStatus()),
                MoneyRestMapper.toResponse(summary.getPaidSales()), summary.getPublishedProducts(),
                summary.getPendingShipments(), summary.getPendingRefunds(), summary.getDamagedInventoryRecords());
    }
}
