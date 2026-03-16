package com.inventorymanagement.exception;

/**
 * Exception thrown when an IN stock movement would cause a warehouse's
 * utilization to exceed its defined capacity.
 *
 * Handled globally to return HTTP 400 BAD_REQUEST.
 */
public class WarehouseCapacityExceededException extends RuntimeException {

    public WarehouseCapacityExceededException(String warehouseName, int quantity,
                                               int currentUtilization, int capacity) {
        super(String.format(
                "Warehouse '%s' capacity exceeded. Capacity: %d, Current utilization: %d, Requested: %d, Available space: %d",
                warehouseName, capacity, currentUtilization, quantity, capacity - currentUtilization));
    }
}
