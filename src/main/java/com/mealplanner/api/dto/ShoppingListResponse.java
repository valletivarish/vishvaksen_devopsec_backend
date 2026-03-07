package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for shopping list responses with all items and their status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListResponse {

    private Long id;
    private String name;
    private Long mealPlanId;
    private String mealPlanName;
    private Long userId;

    /** Individual items with quantities and checked status */
    private List<ShoppingListItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested response DTO for individual shopping list items.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShoppingListItemResponse {
        private Long id;
        private Long ingredientId;
        private String ingredientName;
        private Double quantity;
        private String unit;
        private Boolean checked;
    }
}
