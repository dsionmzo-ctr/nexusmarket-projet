package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Size;

/** Optional justification of an approval or rejection of a refund. */
public record RefundDecisionRequestDTO(
        @Size(max = 500) String note
) {
}
