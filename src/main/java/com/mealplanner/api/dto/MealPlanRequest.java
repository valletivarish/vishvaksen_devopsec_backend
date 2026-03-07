package com.mealplanner.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating and updating meal plans with validation.
 * Ensures name, date range, and meal entries are properly specified.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanRequest {

    /** Descriptive name for the meal plan */
    @NotBlank(message = "Meal plan name is required")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    /** Start date of the meal plan period */
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    /** End date must be after start date */
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    /** List of meal entries mapping recipes to day/meal type slots */
    @Valid
    private List<MealPlanEntryRequest> entries;
}
