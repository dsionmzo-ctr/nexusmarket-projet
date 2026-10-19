package application.adapters.security.dtos;

import java.time.Instant;

/**
 * Security metadata carried by an access token. Only what is needed to identify and invalidate a
 * session: no password, e-mail, identification, role or profile data (see SDD, Rest-adapters §3.1).
 *
 * @param subject   immutable internal user identifier (claim sub)
 * @param tokenId   unique token identifier (claim jti)
 * @param version   persisted User.authTokenVersion at issuance time (claim ver)
 * @param issuedAt  issuance instant (claim iat)
 * @param expiresAt expiration instant (claim exp)
 */
public record JwtClaimsDTO(String subject, String tokenId, long version, Instant issuedAt, Instant expiresAt) {
}
