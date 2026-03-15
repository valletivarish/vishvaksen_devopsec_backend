package com.inventorymanagement.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Standard error response structure returned to API clients for all error scenarios.
 *
 * Every exception handled by {@link GlobalExceptionHandler} is translated into this
 * uniform shape so that consumers of the API can rely on a consistent contract
 * regardless of the underlying error type.
 *
 * Fields:
 * - status    : the HTTP status code (e.g., 404, 409, 500)
 * - message   : a human-readable summary of the error
 * - timestamp : ISO-8601 formatted date-time indicating when the error occurred
 * - errors    : an optional map of field-level validation errors (field name -> message);
 *               present only when request body validation fails, null otherwise
 */
@Data
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private String timestamp;
    private Map<String, String> errors;
}
