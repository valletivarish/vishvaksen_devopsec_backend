package com.mealplanner.api.exception;

/**
 * Custom exception thrown when a requested resource (entity) cannot be
 * found in the database. Returns HTTP 404 when handled by the global
 * exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /** Convenience constructor for entity type and ID-based lookups */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
