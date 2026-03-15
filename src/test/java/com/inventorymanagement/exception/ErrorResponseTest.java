package com.inventorymanagement.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ErrorResponse}.
 *
 * Verifies builder and Lombok-generated methods.
 */
class ErrorResponseTest {

    @Test
    @DisplayName("Builder creates ErrorResponse with all fields")
    void testBuilder() {
        Map<String, String> errors = Map.of("name", "must not be blank");

        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .message("Validation failed")
                .timestamp("2025-01-01T00:00:00")
                .errors(errors)
                .build();

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("Validation failed");
        assertThat(response.getTimestamp()).isEqualTo("2025-01-01T00:00:00");
        assertThat(response.getErrors()).containsEntry("name", "must not be blank");
    }

    @Test
    @DisplayName("Setters modify ErrorResponse fields correctly")
    void testSetters() {
        ErrorResponse response = ErrorResponse.builder().status(200).message("OK").build();
        response.setStatus(500);
        response.setMessage("Internal Server Error");
        response.setTimestamp("2025-06-15T12:00:00");
        response.setErrors(null);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMessage()).isEqualTo("Internal Server Error");
        assertThat(response.getTimestamp()).isEqualTo("2025-06-15T12:00:00");
        assertThat(response.getErrors()).isNull();
    }

    @Test
    @DisplayName("Equals and hashCode work correctly")
    void testEqualsAndHashCode() {
        ErrorResponse r1 = ErrorResponse.builder().status(404).message("Not found").build();
        ErrorResponse r2 = ErrorResponse.builder().status(404).message("Not found").build();

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    @DisplayName("toString contains field values")
    void testToString() {
        ErrorResponse response = ErrorResponse.builder().status(409).message("Conflict").build();
        assertThat(response.toString()).contains("409").contains("Conflict");
    }
}
