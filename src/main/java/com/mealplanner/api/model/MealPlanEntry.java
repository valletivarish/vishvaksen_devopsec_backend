package com.mealplanner.api.model;

import com.mealplanner.api.model.enums.MealType;
import jakarta.persistence.*;
import lombok.*;

/**
 * MealPlanEntry links a specific recipe to a day and meal type
 * within a meal plan. For example, "Oatmeal for Monday Breakfast".
 * Uses Java's built-in DayOfWeek enum for day representation.
 */
@Entity
@Table(name = "meal_plan_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The meal plan this entry belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    /** The recipe assigned to this meal slot */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    /** Day of the week for this meal (MONDAY through SUNDAY) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private java.time.DayOfWeek dayOfWeek;

    /** Type of meal (BREAKFAST, LUNCH, DINNER, SNACK) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;
}
