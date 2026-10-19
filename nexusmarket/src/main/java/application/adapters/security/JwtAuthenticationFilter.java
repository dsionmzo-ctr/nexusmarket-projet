package application.adapters.security;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.adapters.security.dtos.JwtClaimsDTO;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates every request that carries {@code Authorization: Bearer <jwt>}.
 *
 * <p>It verifies the token, loads the current user from the authoritative store through
 * {@link SecurityUserLoader} (which rejects missing, inactive or blocked users and obsolete token
 * versions) and publishes an {@link AuthenticatedUserPrincipal}. A request with no valid token simply
 * continues unauthenticated: the authentication entry point answers {@code 401} on protected routes.
 * This filter is created by the security configuration and is intentionally not a Spring bean, so it
 * is not registered twice in the servlet chain.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final SecurityUserLoader securityUserLoader;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, SecurityUserLoader securityUserLoader) {
        this.jwtProvider = jwtProvider;
        this.securityUserLoader = securityUserLoader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);
        if (token != null) {
            try {
                JwtClaimsDTO claims = jwtProvider.getClaims(token);
                AuthenticatedUserPrincipal principal =
                        securityUserLoader.loadActiveUser(claims.subject(), claims.version());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException | JwtException | IllegalArgumentException ex) {
                // Invalid signature, expired token, malformed claims, unknown/inactive user or stale version.
                SecurityContextHolder.clearContext();
            } catch (DataAccessException ex) {
                SecurityContextHolder.clearContext();
                SecurityErrorResponseWriter.write(request, response, 503, "DEPENDENCY_UNAVAILABLE",
                        "A required service is temporarily unavailable.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX) && header.length() > BEARER_PREFIX.length()) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
