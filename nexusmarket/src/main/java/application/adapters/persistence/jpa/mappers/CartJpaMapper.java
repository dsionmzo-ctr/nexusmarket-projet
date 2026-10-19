package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.persistence.jpa.entities.CartJpaEntity;
import application.domain.models.Cart;
import application.domain.models.CartItem;

import java.util.ArrayList;
import java.util.List;

/** Cart Domain Model <-> CartJpaEntity / CartItemJpaEntity. */
public final class CartJpaMapper {

    private CartJpaMapper() {
    }

    public static CartJpaEntity toEntity(Cart domain) {
        CartJpaEntity entity = new CartJpaEntity();
        entity.setId(domain.getId());
        entity.setBuyer(BuyerJpaMapper.ref(domain.getBuyer()));
        entity.setUpdatedAt(domain.getUpdatedAt());
        List<CartItemJpaEntity> items = new ArrayList<>();
        for (CartItem item : domain.getItems()) {
            CartItemJpaEntity itemEntity = new CartItemJpaEntity();
            itemEntity.setCart(entity);
            itemEntity.setProduct(ProductJpaMapper.ref(item.getProduct()));
            itemEntity.setVariant(ProductJpaMapper.variantRef(item.getVariant()));
            itemEntity.setQuantity(item.getQuantity());
            items.add(itemEntity);
        }
        entity.setItems(items);
        return entity;
    }

    public static Cart toDomain(CartJpaEntity entity) {
        Cart domain = new Cart();
        domain.setId(entity.getId());
        domain.setBuyer(BuyerJpaMapper.toDomain(entity.getBuyer()));
        domain.setUpdatedAt(entity.getUpdatedAt());
        List<CartItem> items = new ArrayList<>();
        for (CartItemJpaEntity itemEntity : entity.getItems()) {
            CartItem item = new CartItem();
            item.setProduct(ProductJpaMapper.toDomain(itemEntity.getProduct()));
            item.setVariant(ProductJpaMapper.variantToDomain(itemEntity.getVariant()));
            item.setQuantity(itemEntity.getQuantity());
            items.add(item);
        }
        domain.setItems(items);
        return domain;
    }
}
