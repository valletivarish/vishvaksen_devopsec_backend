package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read-only DTO returned when fetching product details.
 * Includes resolved category and supplier names so the client does not
 * need additional lookups, plus the aggregated current stock level.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;

    private String name;

    private String sku;

    private String description;

    private BigDecimal unitPrice;

    private Integer reorderLevel;

    /** Resolved category name for display purposes. */
    private Long categoryId;
    private String categoryName;

    /** Resolved supplier name for display purposes. */
    private Long supplierId;
    private String supplierName;

    /**
     * Aggregated stock across all warehouses.
     * Computed at query time from stock-movement records.
     */
    private Integer currentStock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
