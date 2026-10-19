package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.RefundDecisionRequestDTO;
import application.adapters.rest.dtos.responses.RefundResponseDTO;
import application.domain.models.Refund;
import application.domain.valueobjects.RefundStatus;

/** Refund Domain Model <-> refund DTO and query filter. */
public final class RefundRestMapper {

    private RefundRestMapper() {
    }

    /** Decision request: identifier plus the optional justification. */
    public static Refund toDecision(Long refundId, RefundDecisionRequestDTO dto) {
        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setDecisionNote(dto == null ? null : dto.note());
        return refund;
    }

    /** Filter by status; a null or blank status means "all refunds". */
    public static Refund toFilter(String statusCode) {
        Refund filter = new Refund();
        if (statusCode != null && !statusCode.isBlank()) {
            filter.setStatus(RefundStatus.fromCode(statusCode));
        }
        return filter;
    }

    public static RefundResponseDTO toResponse(Refund refund) {
        return new RefundResponseDTO(refund.getId(), refund.getProductReturn().getId(),
                MoneyRestMapper.toResponse(refund.getAmount()), refund.getStatus().getCode(), refund.getRequestedAt(),
                refund.getDecidedAt(), refund.getDecidedBy() == null ? null : refund.getDecidedBy().getId(),
                refund.getDecisionNote());
    }
}
