package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only DTO returned when fetching supplier details.
 * Includes a computed product count indicating how many products
 * are sourced from this supplier.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponseDto {

    private Long id;

    private String name;

    private String contactEmail;

    private String phone;

    private String address;

    /** Number of products supplied by this supplier. */
    private Long productCount;

    private LocalDateTime createdAt;
}
