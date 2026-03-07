package com.mealplanner.api.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Join entity linking recipes to ingredients with specific quantities.
 * Tracks how much of each ingredient is needed for a recipe,
 * enabling nutritional calculations per serving.
 */
@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The recipe this ingredient belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    /** The ingredient being used in the recipe */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    /** Amount of the ingredient needed (in the specified unit) */
    @Column(nullable = false)
    private Double quantity;

    /** Measurement unit for this specific usage (e.g., grams, cups, tablespoons) */
    @Column(nullable = false, length = 50)
    private String unit;
}
