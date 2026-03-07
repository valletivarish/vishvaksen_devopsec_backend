package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for dietary profile responses with all goals and restrictions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDietaryProfileResponse {

    private Long id;
    private Long userId;
    private Integer calorieGoal;
    private Double proteinGoal;
    private Double carbGoal;
    private Double fatGoal;
    private List<String> allergies;
    private List<String> dietaryRestrictions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
