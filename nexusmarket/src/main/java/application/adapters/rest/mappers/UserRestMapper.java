package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.LoginRequestDTO;
import application.adapters.rest.dtos.requests.RegisterSellerUserRequestDTO;
import application.adapters.rest.dtos.requests.RegisterStaffUserRequestDTO;
import application.adapters.rest.dtos.requests.RegisterUserRequestDTO;
import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/** User Domain Model <-> user DTOs. The password only travels from request to domain, never back. */
public final class UserRestMapper {

    private UserRestMapper() {
    }

    public static User toDomain(LoginRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.email().trim().toLowerCase());
        user.setRawPassword(dto.password());
        return user;
    }

    public static User toBuyerUser(RegisterUserRequestDTO dto) {
        User user = new User();
        Buyer buyer = new Buyer();
        buyer.setIdentification(dto.buyerIdentification().trim());
        user.setBuyer(buyer);
        user.setRawPassword(dto.password());
        return user;
    }

    public static User toSellerUser(RegisterSellerUserRequestDTO dto) {
        User user = new User();
        Seller seller = new Seller();
        seller.setIdentification(dto.sellerIdentification().trim());
        user.setSeller(seller);
        user.setRawPassword(dto.password());
        return user;
    }

    public static User toStaffUser(RegisterStaffUserRequestDTO dto) {
        User user = new User();
        user.setIdentification(dto.identification().trim());
        user.setName(dto.name().trim());
        user.setEmail(dto.email().trim());
        user.setRole(SystemRole.fromCode(dto.role()));
        user.setRawPassword(dto.password());
        return user;
    }

    /** Target of a status change: identifier plus the requested catalog code. */
    public static User toStatusTarget(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    public static UserStatus toStatus(String code) {
        return UserStatus.fromCode(code);
    }

    public static UserResponseDTO toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(user.getId(), user.getIdentification(), user.getName(), user.getEmail(),
                user.getRole() == null ? null : user.getRole().getCode(),
                user.getStatus() == null ? null : user.getStatus().getCode());
    }

    public static LoginResponseDTO toResponse(AuthenticationResult result) {
        return new LoginResponseDTO(result.getToken(), "Bearer", result.getExpiresIn(),
                toResponse(result.getAuthenticatedUser()));
    }
}
