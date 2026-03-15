package com.inventorymanagement.dto;

import com.inventorymanagement.model.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only DTO returned when fetching stock movement records.
 * Includes resolved product and warehouse names so the client can
 * render movement history without additional lookups.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponseDto {

    private Long id;

    private Long productId;

    /** Resolved product name for display purposes. */
    private String productName;

    private Long warehouseId;

    /** Resolved warehouse name for display purposes. */
    private String warehouseName;

    private Integer quantity;

    private MovementType type;

    private String referenceNumber;

    private String notes;

    /** Timestamp when the movement was recorded in the system. */
    private LocalDateTime movementDate;
}
