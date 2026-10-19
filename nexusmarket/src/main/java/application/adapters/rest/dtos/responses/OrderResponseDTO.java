package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;

/** Order with its lines. */
public record OrderResponseDTO(
        Long id,
        Long buyerId,
        String status,
        AddressResponseDTO shippingAddress,
        String paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime deliveredAt,
        List<OrderItemResponseDTO> items,
        MoneyResponseDTO total
) {
}
