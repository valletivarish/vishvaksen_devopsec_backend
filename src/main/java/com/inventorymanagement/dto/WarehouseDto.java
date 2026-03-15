package com.inventorymanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a warehouse.
 * Warehouses are physical storage locations that hold product stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {

    /** Warehouse display name (e.g., "Main Distribution Center"). */
    @NotBlank(message = "Warehouse name is required")
    @Size(max = 200, message = "Warehouse name must not exceed 200 characters")
    private String name;

    /** Physical address or description of the warehouse location. */
    @NotBlank(message = "Location is required")
    @Size(max = 500, message = "Location must not exceed 500 characters")
    private String location;

    /**
     * Maximum number of stock units the warehouse can hold.
     * Must be at least 1 (a zero-capacity warehouse is meaningless)
     * and at most 1,000,000 to stay within reasonable operational bounds.
     */
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 1000000, message = "Capacity must not exceed 1000000")
    private Integer capacity;
}
