package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only DTO returned when fetching category details.
 * Includes a computed product count so the UI can display how many
 * products belong to each category without a separate query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private Long id;

    private String name;

    private String description;

    /** Number of products currently assigned to this category. */
    private Long productCount;

    private boolean active;

    private LocalDateTime createdAt;
}
