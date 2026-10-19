package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.ports.in.BuyerPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints of the Buyer role (BuyerPort).
 */
@RestController
@RequestMapping("/api/v1/buyer")
public class BuyerRestController {

    private final BuyerPort buyerPort;

    public BuyerRestController(BuyerPort buyerPort) {
        this.buyerPort = buyerPort;
    }

    /** Consults the buyer profile. */
    @GetMapping("/profile")
    public BuyerResponseDTO consultMyProfile(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return BuyerRestMapper.toResponse(buyerPort.consultMyProfile(principal.domainUser()));
    }

    /** Adds an additional delivery address. */
    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public BuyerResponseDTO addAddress(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody AddressRequestDTO request) {
        return BuyerRestMapper.toResponse(buyerPort.addAddress(principal.domainUser(), AddressRestMapper.toDomain(request)));
    }

    /** Removes an additional delivery address (identified in the body). */
    @DeleteMapping("/addresses")
    public BuyerResponseDTO removeAddress(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody AddressRequestDTO request) {
        return BuyerRestMapper.toResponse(buyerPort.removeAddress(principal.domainUser(), AddressRestMapper.toDomain(request)));
    }

    /** Consults the shopping cart. */
    @GetMapping("/cart")
    public CartResponseDTO consultMyCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return CartRestMapper.toResponse(buyerPort.consultMyCart(principal.domainUser()));
    }

    /** Adds a variant to the cart. */
    @PostMapping("/cart/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponseDTO addItemToCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody CartItemRequestDTO request) {
        return CartRestMapper.toResponse(buyerPort.addItemToCart(principal.domainUser(), CartRestMapper.toItem(request)));
    }

    /** Changes the quantity of a cart line. */
    @PutMapping("/cart/items/{variantId}")
    public CartResponseDTO updateCartItem(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long variantId, @Valid @RequestBody UpdateCartItemRequestDTO request) {
        return CartRestMapper.toResponse(buyerPort.updateCartItem(principal.domainUser(), CartRestMapper.toItem(variantId, request)));
    }

    /** Removes a line from the cart. */
    @DeleteMapping("/cart/items/{variantId}")
    public CartResponseDTO removeCartItem(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long variantId) {
        return CartRestMapper.toResponse(buyerPort.removeCartItem(principal.domainUser(), CartRestMapper.toItem(variantId, 0)));
    }

    /** Empties the cart. */
    @DeleteMapping("/cart")
    public CartResponseDTO clearCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return CartRestMapper.toResponse(buyerPort.clearCart(principal.domainUser()));
    }

    /** Converts the cart into an order pending payment and reserves stock. */
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO checkout(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody(required = false) CheckoutRequestDTO request) {
        return OrderRestMapper.toResponse(buyerPort.checkout(principal.domainUser(), OrderRestMapper.toCheckout(request)));
    }

    /** Pays an order; issues the invoice and starts the logistic flow. */
    @PatchMapping("/orders/{orderId}/payment")
    public OrderResponseDTO payOrder(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId, @Valid @RequestBody PayOrderRequestDTO request) {
        return OrderRestMapper.toResponse(buyerPort.payOrder(principal.domainUser(), OrderRestMapper.toPayment(orderId, request)));
    }

    /** Cancels an order pending payment and releases its stock. */
    @PatchMapping("/orders/{orderId}/cancel")
    public OrderResponseDTO cancelOrder(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return OrderRestMapper.toResponse(buyerPort.cancelOrder(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Lists the buyer's orders. */
    @GetMapping("/orders")
    public List<OrderResponseDTO> consultMyOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return buyerPort.consultMyOrders(principal.domainUser()).stream().map(OrderRestMapper::toResponse).toList();
    }

    /** Consults one of the buyer's orders. */
    @GetMapping("/orders/{orderId}")
    public OrderResponseDTO consultMyOrder(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return OrderRestMapper.toResponse(buyerPort.consultMyOrder(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Consults the invoice of an order. */
    @GetMapping("/orders/{orderId}/invoice")
    public InvoiceResponseDTO consultMyInvoice(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return InvoiceRestMapper.toResponse(buyerPort.consultMyInvoice(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Consults the shipment of an order. */
    @GetMapping("/orders/{orderId}/shipment")
    public ShipmentResponseDTO consultMyShipment(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return ShipmentRestMapper.toResponse(buyerPort.consultMyShipment(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Requests the return of purchased goods (generates a refund request). */
    @PostMapping("/returns")
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnRequestResponseDTO requestReturn(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody RequestReturnRequestDTO request) {
        return ReturnRestMapper.toResponse(buyerPort.requestReturn(principal.domainUser(), ReturnRestMapper.toDomain(request)));
    }

    /** Lists the buyer's returns. */
    @GetMapping("/returns")
    public List<ProductReturnResponseDTO> consultMyReturns(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return buyerPort.consultMyReturns(principal.domainUser()).stream().map(ReturnRestMapper::toResponse).toList();
    }

    /** Lists the buyer's refunds. */
    @GetMapping("/refunds")
    public List<RefundResponseDTO> consultMyRefunds(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return buyerPort.consultMyRefunds(principal.domainUser()).stream().map(RefundRestMapper::toResponse).toList();
    }

    /** Lists the operations executed by the buyer. */
    @GetMapping("/operations")
    public List<OperationResponseDTO> consultMyOperations(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return buyerPort.consultMyOperations(principal.domainUser()).stream().map(OperationRestMapper::toResponse).toList();
    }
}
