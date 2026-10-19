package application.adapters.security;

import application.adapters.security.dtos.JwtClaimsDTO;
import application.adapters.security.mappers.JwtClaimsMapper;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Output adapter that issues and validates JWT access tokens (HS256).
 *
 * <p>Claims: {@code sub} (immutable user id), {@code jti}, {@code ver} ({@code User.authTokenVersion}),
 * {@code iat} and {@code exp}, plus the configured issuer. The role and the user status are never read
 * from the token: they are loaded from the authoritative store on every request.
 */
@Component
public class JwtProvider implements JwtServicePort {

    private static final int MINIMUM_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final long expirationMs;
    private final String issuer;
    private final Clock clock;

    public JwtProvider(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs,
            @Value("${jwt.issuer:nexusmarket}") String issuer,
            Clock clock) {
        byte[] keyBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException("jwt.secret must contain at least " + MINIMUM_SECRET_BYTES + " bytes.");
        }
        if (expirationMs <= 0) {
            throw new IllegalStateException("jwt.expiration-ms must be positive.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
        this.issuer = issuer;
        this.clock = clock;
    }

    @Override
    public String generateToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("A persisted user must be provided to issue a token.");
        }
        Instant now = clock.instant();
        JwtBuilder builder = Jwts.builder()
                .issuer(issuer)
                .subject(JwtClaimsMapper.toSubject(user))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)));
        JwtClaimsMapper.toCustomClaims(user).forEach(builder::claim);
        return builder.signWith(signingKey).compact();
    }

    @Override
    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    /** True when the signature, issuer and expiration are valid and the required claims are present. */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /** Verifies the token and returns its claims. Throws a runtime exception when it is not valid. */
    public JwtClaimsDTO getClaims(String token) {
        return JwtClaimsMapper.toDto(Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }
}
