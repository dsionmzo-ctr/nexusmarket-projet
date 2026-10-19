package application.adapters.rest.exception;

import application.adapters.rest.dtos.responses.ErrorResponseDTO;
import application.adapters.security.RequestIdFilter;
import application.domain.exceptions.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single error contract of the REST API (SDD, Global exception handler). Every handled failure is
 * answered with the {@link ErrorResponseDTO} envelope and the request correlation id. Unexpected
 * failures never expose messages, stack traces, SQL or internal details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    // ---------------------------------------------------------------- domain

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDTO> handleDomain(DomainException ex, HttpServletRequest request) {
        ErrorCodeMapping.Mapping mapping = ErrorCodeMapping.forDomainException(ex);
        if (mapping.status() >= 500) {
            return unexpected(ex, request);
        }
        return build(request, mapping.status(), mapping.code(), ex.getMessage(), null);
    }

    // ------------------------------------------------------------ validation

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleBodyValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<Map<String, String>> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(detail(error.getField(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            details.add(detail(error.getObjectName(), error.getDefaultMessage()));
        }
        return build(request, 400, "INVALID_REQUEST", "The request contains invalid data.", details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleParameterValidation(HandlerMethodValidationException ex,
            HttpServletRequest request) {
        List<Map<String, String>> details = new ArrayList<>();
        ex.getParameterValidationResults().forEach(result -> {
            String parameter = result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                details.add(detail(parameter, error.getDefaultMessage()));
            }
        });
        return build(request, 400, "INVALID_REQUEST", "The request contains invalid data.", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest request) {
        List<Map<String, String>> details = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            details.add(detail(String.valueOf(violation.getPropertyPath()), violation.getMessage()));
        }
        return build(request, 400, "INVALID_REQUEST", "The request contains invalid data.", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableBody(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return build(request, 400, "INVALID_REQUEST", "The request body is missing, malformed or has wrong types.", null);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseDTO> handleParameterShape(Exception ex, HttpServletRequest request) {
        return build(request, 400, "INVALID_REQUEST", "A path or query parameter is missing or has an invalid format.", null);
    }

    // -------------------------------------------------------------- security

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(request, 403, "FORBIDDEN", "The authenticated user is not allowed to perform this operation.", null);
    }

    // ----------------------------------------------------------- persistence

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        if (ex instanceof DuplicateKeyException || isUniqueViolation(ex)) {
            return build(request, 409, "RESOURCE_ALREADY_EXISTS", "The resource already exists.", null);
        }
        return unexpected(ex, request);
    }

    @ExceptionHandler({DataAccessResourceFailureException.class, CannotCreateTransactionException.class,
            TransientDataAccessException.class})
    public ResponseEntity<ErrorResponseDTO> handleDependencyUnavailable(Exception ex, HttpServletRequest request) {
        LOGGER.error("Dependency unavailable [requestId={}]: {}", requestId(request), ex.getClass().getSimpleName(), ex);
        return build(request, 503, "DEPENDENCY_UNAVAILABLE", "A required service is temporarily unavailable.", null);
    }

    // ------------------------------------------------------------- fallbacks

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAny(Exception ex, HttpServletRequest request) {
        if (ex instanceof ErrorResponse framework) {
            ErrorCodeMapping.Mapping mapping = ErrorCodeMapping.forHttpStatus(framework.getStatusCode().value());
            if (mapping.status() < 500) {
                return build(request, mapping.status(), mapping.code(), genericMessage(mapping.status()), null);
            }
        }
        return unexpected(ex, request);
    }

    // --------------------------------------------------------------- helpers

    private ResponseEntity<ErrorResponseDTO> unexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unexpected failure [requestId={}] on {}", requestId(request), request.getRequestURI(), ex);
        return build(request, 500, "INTERNAL_ERROR", "An unexpected error occurred.", null);
    }

    private ResponseEntity<ErrorResponseDTO> build(HttpServletRequest request, int status, String code,
            String message, Object details) {
        ErrorResponseDTO body = new ErrorResponseDTO(OffsetDateTime.now(clock.withZone(ZoneOffset.UTC)).format(TIMESTAMP),
                status, code, message, request.getRequestURI(), requestId(request), details);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    private static String requestId(HttpServletRequest request) {
        Object id = request.getAttribute(RequestIdFilter.ATTRIBUTE);
        return id == null ? null : id.toString();
    }

    private static Map<String, String> detail(String field, String message) {
        Map<String, String> detail = new LinkedHashMap<>();
        detail.put("field", field);
        detail.put("message", message);
        return detail;
    }

    private static String genericMessage(int status) {
        return switch (status) {
            case 404 -> "Requested resource was not found.";
            case 405 -> "The HTTP method is not supported for this resource.";
            case 406 -> "The requested response format is not supported.";
            case 415 -> "The request content type is not supported.";
            default -> "The request is not valid.";
        };
    }

    private static boolean isUniqueViolation(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql && UNIQUE_VIOLATION_SQL_STATE.equals(sql.getSQLState())) {
                return true;
            }
            if (cause.getCause() == cause) {
                break;
            }
        }
        return false;
    }
}
