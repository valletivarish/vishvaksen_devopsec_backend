package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO that aggregates key metrics for the dashboard view.
 * Provides a single-request snapshot of the inventory system's health,
 * reducing the number of API calls the frontend needs on initial load.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {

    /** Total number of distinct products in the system. */
    private long totalProducts;

    /** Total number of product categories. */
    private long totalCategories;

    /** Total number of warehouses. */
    private long totalWarehouses;

    /** Total number of registered suppliers. */
    private long totalSuppliers;

    /**
     * Products whose current stock level is at or below their reorder level.
     * Used to surface restocking alerts on the dashboard.
     */
    private List<ProductResponseDto> lowStockProducts;

    /**
     * Most recent stock movements across all warehouses.
     * Typically limited to the last N records for quick overview.
     */
    private List<StockMovementResponseDto> recentMovements;

    /**
     * Sum of (unitPrice * currentStock) across all products.
     * Gives a high-level financial snapshot of total inventory value.
     */
    private BigDecimal totalStockValue;
}
