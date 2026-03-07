package com.mealplanner.api.exception;

/**
 * Custom exception thrown when attempting to create a resource that
 * already exists (e.g., duplicate username or email during registration).
 * Returns HTTP 409 Conflict when handled by the global exception handler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
