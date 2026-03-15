package com.inventorymanagement.exception;

/**
 * Exception thrown when an inventory operation requests more units of a product
 * than are currently available in stock.
 *
 * This guards against negative inventory and ensures that order or dispatch
 * operations fail fast with a clear explanation rather than corrupting stock
 * levels.
 *
 * Handled globally to return HTTP 400 BAD_REQUEST.
 */
public class InsufficientStockException extends RuntimeException {

    private final String productName;
    private final int requested;
    private final int available;

    /**
     * Constructs a new InsufficientStockException.
     *
     * @param productName the name of the product with insufficient stock
     * @param requested   the number of units requested
     * @param available   the number of units currently available
     */
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Insufficient stock for %s. Requested: %d, Available: %d",
                productName, requested, available));
        this.productName = productName;
        this.requested = requested;
        this.available = available;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
