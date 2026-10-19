package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.OrderItemJpaEntity;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.StockAllocationJpaEntity;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.StockAllocation;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.ProductType;

import java.util.ArrayList;
import java.util.List;

/** Order Domain Model (with lines and stock allocations) <-> OrderJpaEntity and its children. */
public final class OrderJpaMapper {

    private OrderJpaMapper() {
    }

    public static OrderJpaEntity toEntity(Order domain) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(domain.getId());
        entity.setBuyer(BuyerJpaMapper.ref(domain.getBuyer()));
        entity.setStatus(domain.getStatus().getCode());
        entity.setShippingAddress(AddressJpaMapper.toEmbeddable(domain.getShippingAddress()));
        entity.setPaymentMethod(domain.getPaymentMethod() == null ? null : domain.getPaymentMethod().getCode());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setPaidAt(domain.getPaidAt());
        entity.setDeliveredAt(domain.getDeliveredAt());
        List<OrderItemJpaEntity> items = new ArrayList<>();
        for (OrderItem item : domain.getItems()) {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
            itemEntity.setId(item.getId());
            itemEntity.setOrder(entity);
            itemEntity.setProduct(ProductJpaMapper.ref(item.getProduct()));
            itemEntity.setVariant(ProductJpaMapper.variantRef(item.getVariant()));
            itemEntity.setSeller(SellerJpaMapper.ref(item.getSeller()));
            itemEntity.setProductName(item.getProductName());
            itemEntity.setSku(item.getSku());
            itemEntity.setProductType(item.getProductType().getCode());
            itemEntity.setUnitPriceAmount(item.getUnitPrice().amount());
            itemEntity.setUnitPriceCurrency(item.getUnitPrice().currency().getCode());
            itemEntity.setQuantity(item.getQuantity());
            List<StockAllocationJpaEntity> allocations = new ArrayList<>();
            for (StockAllocation allocation : item.getAllocations()) {
                StockAllocationJpaEntity allocationEntity = new StockAllocationJpaEntity();
                allocationEntity.setOrderItem(itemEntity);
                allocationEntity.setInventory(InventoryJpaMapper.ref(allocation.getInventory()));
                allocationEntity.setQuantity(allocation.getQuantity());
                allocations.add(allocationEntity);
            }
            itemEntity.setAllocations(allocations);
            items.add(itemEntity);
        }
        entity.setItems(items);
        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity) {
        Order domain = new Order();
        domain.setId(entity.getId());
        domain.setBuyer(BuyerJpaMapper.toDomain(entity.getBuyer()));
        domain.setStatus(OrderStatus.fromCode(entity.getStatus()));
        domain.setShippingAddress(AddressJpaMapper.toDomain(entity.getShippingAddress()));
        domain.setPaymentMethod(entity.getPaymentMethod() == null ? null : PaymentMethod.fromCode(entity.getPaymentMethod()));
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setPaidAt(entity.getPaidAt());
        domain.setDeliveredAt(entity.getDeliveredAt());
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemJpaEntity itemEntity : entity.getItems()) {
            items.add(itemToDomain(itemEntity));
        }
        domain.setItems(items);
        return domain;
    }

    public static OrderItem itemToDomain(OrderItemJpaEntity itemEntity) {
        OrderItem item = new OrderItem();
        item.setId(itemEntity.getId());
        item.setProduct(ProductJpaMapper.toShallowDomain(itemEntity.getProduct()));
        item.setVariant(ProductJpaMapper.variantToDomain(itemEntity.getVariant()));
        item.setSeller(SellerJpaMapper.toDomain(itemEntity.getSeller()));
        item.setProductName(itemEntity.getProductName());
        item.setSku(itemEntity.getSku());
        item.setProductType(ProductType.fromCode(itemEntity.getProductType()));
        item.setUnitPrice(MoneyJpaMapper.toDomain(itemEntity.getUnitPriceAmount(), itemEntity.getUnitPriceCurrency()));
        item.setQuantity(itemEntity.getQuantity());
        List<StockAllocation> allocations = new ArrayList<>();
        for (StockAllocationJpaEntity allocationEntity : itemEntity.getAllocations()) {
            allocations.add(new StockAllocation(InventoryJpaMapper.idOnly(allocationEntity.getInventory()),
                    allocationEntity.getQuantity()));
        }
        item.setAllocations(allocations);
        return item;
    }

    public static OrderJpaEntity ref(Order domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }

    public static OrderItemJpaEntity itemRef(OrderItem domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
