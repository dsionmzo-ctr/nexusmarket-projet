package application.adapters.security.mappers;

import application.adapters.security.dtos.JwtClaimsDTO;
import application.domain.models.User;
import io.jsonwebtoken.Claims;

import java.util.LinkedHashMap;
import java.util.Map;

/** User Domain Model <-> JWT claims. Only subject, version and standard claims are ever mapped. */
public final class JwtClaimsMapper {

    public static final String VERSION_CLAIM = "ver";

    private JwtClaimsMapper() {
    }

    /** Custom claims to add to the token (the standard ones are set by the provider). */
    public static Map<String, Object> toCustomClaims(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put(VERSION_CLAIM, user.getAuthTokenVersion());
        return claims;
    }

    public static String toSubject(User user) {
        return String.valueOf(user.getId());
    }

    /** Parsed claims -> DTO. Throws IllegalArgumentException when a required claim is missing or malformed. */
    public static JwtClaimsDTO toDto(Claims claims) {
        Object version = claims.get(VERSION_CLAIM);
        if (claims.getSubject() == null || claims.getId() == null || !(version instanceof Number number)
                || claims.getIssuedAt() == null || claims.getExpiration() == null) {
            throw new IllegalArgumentException("Token is missing required claims.");
        }
        return new JwtClaimsDTO(claims.getSubject(), claims.getId(), number.longValue(),
                claims.getIssuedAt().toInstant(), claims.getExpiration().toInstant());
    }

    /** Subject -> user identifier, or null when the subject is not a number. */
    public static Long extractUserId(String subject) {
        try {
            return subject == null ? null : Long.valueOf(subject);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
