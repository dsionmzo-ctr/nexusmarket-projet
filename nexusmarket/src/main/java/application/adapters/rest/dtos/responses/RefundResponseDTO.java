package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

/** Refund associated to a return. */
public record RefundResponseDTO(
        Long id,
        Long returnId,
        MoneyResponseDTO amount,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime decidedAt,
        Long decidedById,
        String decisionNote
) {
}
