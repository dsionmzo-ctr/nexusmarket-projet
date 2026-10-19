package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

/** Invoice of a paid order. */
public record InvoiceResponseDTO(
        Long id,
        String invoiceNumber,
        Long orderId,
        MoneyResponseDTO subtotal,
        MoneyResponseDTO taxAmount,
        MoneyResponseDTO total,
        LocalDateTime issuedAt
) {
}
