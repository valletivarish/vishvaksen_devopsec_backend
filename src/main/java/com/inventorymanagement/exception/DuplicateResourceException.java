package com.inventorymanagement.exception;

/**
 * Exception thrown when an attempt is made to create a resource that conflicts
 * with an existing one.
 *
 * Typical triggers include inserting a product with a SKU that is already in use
 * or registering a supplier with a duplicate email address. The exception
 * captures the resource type, the conflicting field, and its value so the API
 * can return a precise error message to the client.
 *
 * Handled globally to return HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new DuplicateResourceException.
     *
     * @param resourceName the type of resource that already exists (e.g., "Product", "Category")
     * @param fieldName    the field that caused the conflict (e.g., "sku", "email")
     * @param fieldValue   the duplicate value
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue));
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
