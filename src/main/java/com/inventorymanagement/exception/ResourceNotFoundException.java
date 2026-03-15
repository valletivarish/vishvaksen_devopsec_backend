package com.inventorymanagement.exception;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 *
 * This is used across all service layers to signal that a lookup by a specific
 * field (e.g., ID, SKU, name) yielded no result. The exception carries enough
 * context to produce a clear, user-facing error message without leaking
 * internal implementation details.
 *
 * Handled globally to return HTTP 404 NOT_FOUND.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new ResourceNotFoundException.
     *
     * @param resourceName the type of resource that was not found (e.g., "Product", "Supplier")
     * @param fieldName    the field used in the lookup (e.g., "id", "sku")
     * @param fieldValue   the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
