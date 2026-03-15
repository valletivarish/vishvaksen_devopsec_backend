package com.inventorymanagement.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for the entire REST API.
 *
 * By using {@code @RestControllerAdvice}, every controller in the application
 * benefits from consistent error handling without duplicating try-catch logic.
 * Each handler method maps a specific exception type to an appropriate HTTP
 * status code and returns an {@link ErrorResponse} so that API consumers always
 * receive a predictable error structure.
 *
 * Security note: the catch-all handler for {@link Exception} deliberately hides
 * internal details (stack traces, class names, database errors) from the client.
 * Exposing such information can aid attackers in reconnaissance. The full error
 * is logged server-side for debugging purposes instead.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Returns the current timestamp in ISO-8601 format for inclusion in error responses.
     */
    private String currentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // -------------------------------------------------------------------------
    // Domain-specific exceptions
    // -------------------------------------------------------------------------

    /**
     * Handles cases where a requested resource (product, supplier, category, etc.)
     * does not exist. Returns HTTP 404 so the client knows the resource was not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(currentTimestamp())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles attempts to create a resource that conflicts with an existing one
     * (e.g., duplicate SKU). Returns HTTP 409 to indicate the conflict.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(currentTimestamp())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles inventory shortfalls where the requested quantity exceeds available stock.
     * Returns HTTP 400 because the request itself is invalid given current stock levels.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(currentTimestamp())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------------------------
    // Validation and deserialization exceptions
    // -------------------------------------------------------------------------

    /**
     * Handles Bean Validation failures on @Valid-annotated request bodies.
     *
     * Each field error is extracted and placed into a map so the client can display
     * per-field messages (e.g., "name: must not be blank", "price: must be positive").
     * Returns HTTP 400 because the client sent invalid data.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed. Check 'errors' for details.")
                .timestamp(currentTimestamp())
                .errors(fieldErrors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles constraint violations triggered by validation annotations on method
     * parameters or path variables (e.g., @Min, @NotBlank on a @RequestParam).
     * Returns HTTP 400 because the client supplied values that violate constraints.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            // Extract the simple property name from the full path (e.g., "getProduct.id" -> "id")
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.contains(".")
                    ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                    : propertyPath;
            fieldErrors.put(field, violation.getMessage());
        });

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed. Check 'errors' for details.")
                .timestamp(currentTimestamp())
                .errors(fieldErrors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed or unreadable JSON in request bodies.
     *
     * The raw parsing error message is not forwarded to the client because it may
     * reveal internal class names or structure. A generic message is returned instead.
     * Returns HTTP 400 because the request body could not be deserialized.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON received: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Malformed JSON request. Please check the request body.")
                .timestamp(currentTimestamp())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------------------------
    // Catch-all handler
    // -------------------------------------------------------------------------

    /**
     * Catches any unhandled exception that slips through the specific handlers above.
     *
     * Security best practice: the actual exception message and stack trace are logged
     * on the server but are NEVER sent to the client. Exposing internal error details
     * (e.g., SQL statements, class names, file paths) can give attackers valuable
     * information about the application's internals. The client receives only a safe,
     * generic message along with HTTP 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(currentTimestamp())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
