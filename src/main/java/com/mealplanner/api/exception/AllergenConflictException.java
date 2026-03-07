package com.mealplanner.api.exception;

/**
 * Custom exception thrown when a recipe contains ingredients that
 * conflict with the user's declared food allergies.
 * Returns HTTP 400 Bad Request when handled by the global exception handler.
 */
public class AllergenConflictException extends RuntimeException {

    public AllergenConflictException(String message) {
        super(message);
    }
}
