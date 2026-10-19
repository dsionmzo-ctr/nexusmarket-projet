package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.ReceiveReturnRequestDTO;
import application.adapters.rest.dtos.requests.RequestReturnRequestDTO;
import application.adapters.rest.dtos.responses.ProductReturnResponseDTO;
import application.adapters.rest.dtos.responses.ReturnRequestResponseDTO;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.ProductReturn;
import application.domain.models.ProductVariant;
import application.domain.models.ReturnRequestResult;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ReturnReason;

/** ProductReturn Domain Model <-> return DTOs. */
public final class ReturnRestMapper {

    private ReturnRestMapper() {
    }

    public static ProductReturn toDomain(RequestReturnRequestDTO dto) {
        ProductReturn productReturn = new ProductReturn();
        Order order = new Order();
        order.setId(dto.orderId());
        productReturn.setOrder(order);
        OrderItem item = new OrderItem();
        ProductVariant variant = new ProductVariant();
        variant.setId(dto.variantId());
        item.setVariant(variant);
        productReturn.setOrderItem(item);
        productReturn.setQuantity(dto.quantity());
        productReturn.setReason(ReturnReason.fromCode(dto.reason()));
        productReturn.setComment(dto.comment());
        return productReturn;
    }

    public static ProductReturn toProbe(Long returnId) {
        ProductReturn productReturn = new ProductReturn();
        productReturn.setId(returnId);
        return productReturn;
    }

    public static ProductReturn toReception(Long returnId, ReceiveReturnRequestDTO dto) {
        ProductReturn productReturn = toProbe(returnId);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.warehouseId());
        productReturn.setReceivedWarehouse(warehouse);
        return productReturn;
    }

    public static ProductReturnResponseDTO toResponse(ProductReturn productReturn) {
        OrderItem item = productReturn.getOrderItem();
        return new ProductReturnResponseDTO(productReturn.getId(), productReturn.getOrder().getId(),
                item.getVariant().getId(), item.getSku(), item.getProductName(), productReturn.getQuantity(),
                productReturn.getReason().getCode(), productReturn.getComment(), productReturn.getStatus().getCode(),
                productReturn.getRequestedAt(), productReturn.getReceivedAt(),
                productReturn.getReceivedWarehouse() == null ? null : productReturn.getReceivedWarehouse().getId());
    }

    public static ReturnRequestResponseDTO toResponse(ReturnRequestResult result) {
        return new ReturnRequestResponseDTO(toResponse(result.getProductReturn()),
                RefundRestMapper.toResponse(result.getRefund()));
    }
}
