package application.adapters.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Answers 401 AUTHENTICATION_REQUIRED (standard envelope) when a protected route has no valid token. */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        SecurityErrorResponseWriter.write(request, response, 401, "AUTHENTICATION_REQUIRED",
                "A valid bearer token is required.");
    }
}
