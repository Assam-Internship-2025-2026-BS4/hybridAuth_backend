package in.bank.hdfc.auth.hybrid_auth.exception;

import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================================================
       AUTH SESSION / BUSINESS ERRORS
       ========================================================= */

    @ExceptionHandler(AuthSessionException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthSession(AuthSessionException ex) {

        log.warn("Business exception: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("AUTH_400", ex.getMessage()));
    }

    /* =========================================================
       VALIDATION ERRORS
       ========================================================= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("INVALID_REQUEST");

        log.warn("Validation error: {}", message);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("REQ_400", message));
    }

    /* =========================================================
       BAD REQUEST
       ========================================================= */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {

        log.warn("Bad request: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("REQ_400", ex.getMessage()));
    }

    /* =========================================================
       AUTHENTICATION FAILURE
       ========================================================= */

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {

        log.warn("Authentication failed: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure("AUTH_401", "Unauthorized"));
    }

    /* =========================================================
       ACCESS DENIED
       ========================================================= */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {

        log.warn("Access denied: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure("AUTH_403", "Access denied"));
    }

    /* =========================================================
       SYSTEM / UNKNOWN ERRORS
       ========================================================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {

        log.error("Unexpected system error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("SYS_500", "Internal server error"));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMismatch(
            MethodArgumentTypeMismatchException ex) {

        log.warn("Invalid parameter type: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("REQ_400", "INVALID_SESSION_ID"));
    }
}