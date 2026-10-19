package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.ports.in.SellerPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints of the Seller role (SellerPort): own catalog, own inventory and own orders.
 */
@RestController
@RequestMapping("/api/v1/seller")
public class SellerRestController {

    private final SellerPort sellerPort;

    public SellerRestController(SellerPort sellerPort) {
        this.sellerPort = sellerPort;
    }

    /** Registers a product (created suspended until published). */
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO registerProduct(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody RegisterProductRequestDTO request) {
        return ProductRestMapper.toResponse(sellerPort.registerProduct(principal.domainUser(), ProductRestMapper.toDomain(request)));
    }

    /** Updates name, description or price of an own product. */
    @PutMapping("/products/{productId}")
    public ProductResponseDTO updateProduct(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long productId, @Valid @RequestBody UpdateProductRequestDTO request) {
        return ProductRestMapper.toResponse(sellerPort.updateProduct(principal.domainUser(), ProductRestMapper.toChanges(productId, request)));
    }

    /** Adds a variant to an own product. */
    @PostMapping("/products/{productId}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO addProductVariant(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long productId, @Valid @RequestBody ProductVariantRequestDTO request) {
        return ProductRestMapper.toResponse(sellerPort.addProductVariant(principal.domainUser(), ProductRestMapper.toProbe(productId), ProductRestMapper.toVariant(request)));
    }

    /** Publishes an own product in the public catalog. */
    @PatchMapping("/products/{productId}/publish")
    public ProductResponseDTO publishProduct(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long productId) {
        return ProductRestMapper.toResponse(sellerPort.publishProduct(principal.domainUser(), ProductRestMapper.toProbe(productId)));
    }

    /** Suspends an own product. */
    @PatchMapping("/products/{productId}/suspend")
    public ProductResponseDTO suspendProduct(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long productId) {
        return ProductRestMapper.toResponse(sellerPort.suspendProduct(principal.domainUser(), ProductRestMapper.toProbe(productId)));
    }

    /** Discontinues an own product. */
    @PatchMapping("/products/{productId}/discontinue")
    public ProductResponseDTO discontinueProduct(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long productId) {
        return ProductRestMapper.toResponse(sellerPort.discontinueProduct(principal.domainUser(), ProductRestMapper.toProbe(productId)));
    }

    /** Lists the seller's products. */
    @GetMapping("/products")
    public List<ProductResponseDTO> consultMyProducts(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return sellerPort.consultMyProducts(principal.domainUser()).stream().map(ProductRestMapper::toResponse).toList();
    }

    /** Lists the seller's warehouses. */
    @GetMapping("/warehouses")
    public List<WarehouseResponseDTO> consultMyWarehouses(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return sellerPort.consultMyWarehouses(principal.domainUser()).stream().map(WarehouseRestMapper::toResponse).toList();
    }

    /** Registers an inflow of units into one of the seller's warehouses. */
    @PostMapping("/inventory/inflows")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDTO registerInventoryInflow(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @Valid @RequestBody InventoryInflowRequestDTO request) {
        return InventoryRestMapper.toResponse(sellerPort.registerInventoryInflow(principal.domainUser(), InventoryRestMapper.toInflow(request)));
    }

    /** Lists the stock of the seller's products. */
    @GetMapping("/inventory")
    public List<InventoryResponseDTO> consultMyInventory(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return sellerPort.consultMyInventory(principal.domainUser()).stream().map(InventoryRestMapper::toResponse).toList();
    }

    /** Lists the orders that contain the seller's products (only its own lines). */
    @GetMapping("/orders")
    public List<OrderResponseDTO> consultMyOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return sellerPort.consultMyOrders(principal.domainUser()).stream().map(OrderRestMapper::toResponse).toList();
    }

    /** Consults an order that contains the seller's products. */
    @GetMapping("/orders/{orderId}")
    public OrderResponseDTO consultMyOrder(@AuthenticationPrincipal AuthenticatedUserPrincipal principal, @PathVariable @Positive Long orderId) {
        return OrderRestMapper.toResponse(sellerPort.consultMyOrder(principal.domainUser(), OrderRestMapper.toProbe(orderId)));
    }

    /** Lists the operations executed by the seller. */
    @GetMapping("/operations")
    public List<OperationResponseDTO> consultMyOperations(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return sellerPort.consultMyOperations(principal.domainUser()).stream().map(OperationRestMapper::toResponse).toList();
    }
}
