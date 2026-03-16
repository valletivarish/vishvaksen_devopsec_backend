package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only DTO returned when fetching warehouse details.
 * Includes current utilization so the UI can display capacity usage
 * without a separate aggregation query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponseDto {

    private Long id;

    private String name;

    private String location;

    private Integer capacity;

    /**
     * Total stock units currently stored in this warehouse.
     * Computed from stock-movement records at query time.
     */
    private Integer currentUtilization;

    private boolean active;

    private LocalDateTime createdAt;
}
