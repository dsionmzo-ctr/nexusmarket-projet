package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.CheckoutRequestDTO;
import application.adapters.rest.dtos.requests.PayOrderRequestDTO;
import application.adapters.rest.dtos.responses.OrderItemResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.valueobjects.PaymentMethod;

import java.util.List;

/** Order Domain Model <-> order DTOs. */
public final class OrderRestMapper {

    private OrderRestMapper() {
    }

    /** Checkout request: only the optional shipping address is client-provided. */
    public static Order toCheckout(CheckoutRequestDTO dto) {
        Order order = new Order();
        order.setShippingAddress(dto == null ? null : AddressRestMapper.toDomain(dto.shippingAddress()));
        return order;
    }

    public static Order toPayment(Long orderId, PayOrderRequestDTO dto) {
        Order order = toProbe(orderId);
        order.setPaymentMethod(PaymentMethod.fromCode(dto.paymentMethod()));
        return order;
    }

    public static Order toProbe(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        return order;
    }

    public static OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream().map(OrderRestMapper::toResponse).toList();
        return new OrderResponseDTO(order.getId(), order.getBuyer() == null ? null : order.getBuyer().getId(),
                order.getStatus().getCode(), AddressRestMapper.toResponse(order.getShippingAddress()),
                order.getPaymentMethod() == null ? null : order.getPaymentMethod().getCode(),
                order.getCreatedAt(), order.getPaidAt(), order.getDeliveredAt(), items,
                MoneyRestMapper.toResponse(order.total()));
    }

    public static OrderItemResponseDTO toResponse(OrderItem item) {
        return new OrderItemResponseDTO(item.getId(), item.getProduct().getId(), item.getVariant().getId(),
                item.getSeller().getId(), item.getProductName(), item.getSku(), item.getProductType().getCode(),
                MoneyRestMapper.toResponse(item.getUnitPrice()), item.getQuantity(),
                MoneyRestMapper.toResponse(item.lineTotal()));
    }
}
