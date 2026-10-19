package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.domain.ports.in.PublicAccessPort;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Public catalog: published products visible without authentication.
 */
@RestController
@RequestMapping("/api/v1/catalog")
public class PublicCatalogRestController {

    private final PublicAccessPort publicAccessPort;

    public PublicCatalogRestController(PublicAccessPort publicAccessPort) {
        this.publicAccessPort = publicAccessPort;
    }

    /** Lists the published products. */
    @GetMapping("/products")
    public List<ProductResponseDTO> consultPublishedProducts() {
        return publicAccessPort.consultPublishedProducts().stream().map(ProductRestMapper::toResponse).toList();
    }

    /** Consults a published product. */
    @GetMapping("/products/{productId}")
    public ProductResponseDTO consultPublishedProduct(@PathVariable @Positive Long productId) {
        return ProductRestMapper.toResponse(publicAccessPort.consultPublishedProduct(ProductRestMapper.toProbe(productId)));
    }
}
