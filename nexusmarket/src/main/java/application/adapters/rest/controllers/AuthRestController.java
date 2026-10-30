package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.ports.in.PublicAccessPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Public authentication and buyer self-registration endpoints (PublicAccessPort).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final PublicAccessPort publicAccessPort;

    public AuthRestController(PublicAccessPort publicAccessPort) {
        this.publicAccessPort = publicAccessPort;
    }

    /** Authenticates with e-mail and password and issues the access token. */
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return UserRestMapper.toResponse(publicAccessPort.login(UserRestMapper.toDomain(request)));
    }

    /** Invalidates every token issued to the authenticated user (increments authTokenVersion). */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        publicAccessPort.logout(principal.domainUser());
    }

    /** Self-registers a buyer profile together with its access account, so the buyer can log in right away. */
    @PostMapping("/register/buyer")
    @ResponseStatus(HttpStatus.CREATED)
    public BuyerResponseDTO registerBuyer(@Valid @RequestBody RegisterBuyerRequestDTO request) {
        return BuyerRestMapper.toResponse(publicAccessPort.registerBuyerAccount(
                BuyerRestMapper.toDomain(request), UserRestMapper.toBuyerAccount(request)));
    }

    /** Creates the access account of an already registered buyer. */
    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO registerUser(@Valid @RequestBody RegisterUserRequestDTO request) {
        return UserRestMapper.toResponse(publicAccessPort.registerBuyerUser(UserRestMapper.toBuyerUser(request)));
    }
}
