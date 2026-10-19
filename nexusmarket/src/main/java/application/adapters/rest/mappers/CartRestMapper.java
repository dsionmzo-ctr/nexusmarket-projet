package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.CartItemRequestDTO;
import application.adapters.rest.dtos.requests.UpdateCartItemRequestDTO;
import application.adapters.rest.dtos.responses.CartItemResponseDTO;
import application.adapters.rest.dtos.responses.CartResponseDTO;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.ProductVariant;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;

import java.util.List;

/** Cart Domain Model <-> cart DTOs. */
public final class CartRestMapper {

    private CartRestMapper() {
    }

    public static CartItem toItem(CartItemRequestDTO dto) {
        return toItem(dto.variantId(), dto.quantity());
    }

    public static CartItem toItem(Long variantId, UpdateCartItemRequestDTO dto) {
        return toItem(variantId, dto.quantity());
    }

    /** Item that identifies a variant (used by quantity update and removal). */
    public static CartItem toItem(Long variantId, Integer quantity) {
        CartItem item = new CartItem();
        ProductVariant variant = new ProductVariant();
        variant.setId(variantId);
        item.setVariant(variant);
        item.setQuantity(quantity == null ? 0 : quantity);
        return item;
    }

    public static CartResponseDTO toResponse(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream().map(i -> {
            Money unitPrice = i.getProduct().getPrice();
            return new CartItemResponseDTO(i.getProduct().getId(), i.getProduct().getName(), i.getVariant().getId(),
                    i.getVariant().getSku(), MoneyRestMapper.toResponse(unitPrice), i.getQuantity(),
                    MoneyRestMapper.toResponse(unitPrice.multiply(i.getQuantity())));
        }).toList();
        Money total = cart.getItems().stream()
                .map(i -> i.getProduct().getPrice().multiply(i.getQuantity()))
                .reduce(Money::add).orElse(Money.zero(Currency.COP));
        return new CartResponseDTO(cart.getId(), items, MoneyRestMapper.toResponse(total), cart.getUpdatedAt());
    }
}
