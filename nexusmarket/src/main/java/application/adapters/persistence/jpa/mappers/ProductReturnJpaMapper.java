package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ProductReturnJpaEntity;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.ProductReturn;
import application.domain.valueobjects.ReturnReason;
import application.domain.valueobjects.ReturnStatus;

/** ProductReturn Domain Model <-> ProductReturnJpaEntity. */
public final class ProductReturnJpaMapper {

    private ProductReturnJpaMapper() {
    }

    public static ProductReturnJpaEntity toEntity(ProductReturn domain) {
        ProductReturnJpaEntity entity = new ProductReturnJpaEntity();
        entity.setId(domain.getId());
        entity.setOrder(OrderJpaMapper.ref(domain.getOrder()));
        entity.setOrderItem(OrderJpaMapper.itemRef(domain.getOrderItem()));
        entity.setQuantity(domain.getQuantity());
        entity.setReason(domain.getReason().getCode());
        entity.setComment(domain.getComment());
        entity.setStatus(domain.getStatus().getCode());
        entity.setRequestedAt(domain.getRequestedAt());
        entity.setReceivedAt(domain.getReceivedAt());
        entity.setReceivedWarehouse(WarehouseJpaMapper.ref(domain.getReceivedWarehouse()));
        return entity;
    }

    public static ProductReturn toDomain(ProductReturnJpaEntity entity) {
        ProductReturn domain = new ProductReturn();
        domain.setId(entity.getId());
        Order order = OrderJpaMapper.toDomain(entity.getOrder());
        domain.setOrder(order);
        Long itemId = entity.getOrderItem().getId();
        OrderItem item = order.getItems().stream().filter(i -> itemId.equals(i.getId())).findFirst()
                .orElseGet(() -> OrderJpaMapper.itemToDomain(entity.getOrderItem()));
        domain.setOrderItem(item);
        domain.setQuantity(entity.getQuantity());
        domain.setReason(ReturnReason.fromCode(entity.getReason()));
        domain.setComment(entity.getComment());
        domain.setStatus(ReturnStatus.fromCode(entity.getStatus()));
        domain.setRequestedAt(entity.getRequestedAt());
        domain.setReceivedAt(entity.getReceivedAt());
        domain.setReceivedWarehouse(WarehouseJpaMapper.toDomain(entity.getReceivedWarehouse()));
        return domain;
    }

    public static ProductReturnJpaEntity ref(ProductReturn domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        ProductReturnJpaEntity entity = new ProductReturnJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
