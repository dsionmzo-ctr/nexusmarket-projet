package application.adapters.rest.dtos.responses;

/** Result of a return request: the return and the refund it generated. */
public record ReturnRequestResponseDTO(
        ProductReturnResponseDTO productReturn,
        RefundResponseDTO refund
) {
}
