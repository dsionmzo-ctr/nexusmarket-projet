package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.OperationResponseDTO;
import application.domain.models.Operation;

/** Operation Domain Model -> operation DTO. */
public final class OperationRestMapper {

    private OperationRestMapper() {
    }

    public static OperationResponseDTO toResponse(Operation operation) {
        return new OperationResponseDTO(operation.getOperationId(), operation.getOperationType().getCode(),
                operation.getExecutionDate(),
                operation.getPerformedBy() == null ? null : operation.getPerformedBy().getId(),
                operation.getAffectedResource().getResourceType(), operation.getAffectedResource().getId());
    }
}
