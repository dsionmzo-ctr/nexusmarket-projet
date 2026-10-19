package application.adapters.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Writes the standard error envelope for failures raised inside the Spring Security filter chain
 * (where {@code @RestControllerAdvice} does not apply). The JSON is built by hand so the security
 * adapter does not depend on a particular Jackson version; it matches the body of the REST
 * {@code ErrorResponseDTO}.
 */
public final class SecurityErrorResponseWriter {

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private SecurityErrorResponseWriter() {
    }

    public static void write(HttpServletRequest request, HttpServletResponse response, int status,
            String code, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        Object requestId = request.getAttribute(RequestIdFilter.ATTRIBUTE);
        String body = "{\"timestamp\":\"" + OffsetDateTime.now(ZoneOffset.UTC).format(TIMESTAMP) + "\""
                + ",\"status\":" + status
                + ",\"code\":\"" + escape(code) + "\""
                + ",\"message\":\"" + escape(message) + "\""
                + ",\"path\":\"" + escape(request.getRequestURI()) + "\""
                + ",\"requestId\":" + (requestId == null ? "null" : "\"" + escape(requestId.toString()) + "\"")
                + ",\"details\":null}";
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body);
        response.getWriter().flush();
    }

    private static String escape(String value) {
        StringBuilder out = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.toString();
    }
}
