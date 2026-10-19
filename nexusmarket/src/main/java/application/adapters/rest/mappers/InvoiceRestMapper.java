package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.domain.models.Invoice;

/** Invoice Domain Model -> invoice DTO. */
public final class InvoiceRestMapper {

    private InvoiceRestMapper() {
    }

    public static InvoiceResponseDTO toResponse(Invoice invoice) {
        return new InvoiceResponseDTO(invoice.getId(), invoice.getInvoiceNumber(), invoice.getOrder().getId(),
                MoneyRestMapper.toResponse(invoice.getSubtotal()), MoneyRestMapper.toResponse(invoice.getTaxAmount()),
                MoneyRestMapper.toResponse(invoice.getTotal()), invoice.getIssuedAt());
    }
}
