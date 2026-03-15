package com.inventorymanagement.dto;

import com.inventorymanagement.model.MovementType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recording a stock movement (inbound, outbound, or transfer).
 * Each movement adjusts the stock level for a specific product in a specific warehouse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDto {

    /** The product whose stock is being adjusted. Must reference an existing product. */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /** The warehouse where the movement occurs. Must reference an existing warehouse. */
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    /**
     * Number of units moved. Always a positive value --
     * the direction is determined by the movement type (IN, OUT, TRANSFER).
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100000, message = "Quantity must not exceed 100000")
    private Integer quantity;

    /**
     * Direction of the stock movement.
     * IN: goods received into the warehouse.
     * OUT: goods dispatched from the warehouse.
     * TRANSFER: goods moved between warehouses (recorded as OUT from source, IN at destination).
     */
    @NotNull(message = "Movement type is required")
    private MovementType type;

    /**
     * Optional external reference number (e.g., purchase order, shipment ID)
     * for traceability and audit purposes.
     */
    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    private String referenceNumber;

    /** Optional free-text notes providing additional context for the movement. */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

}
