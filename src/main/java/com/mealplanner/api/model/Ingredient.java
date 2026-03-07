package com.mealplanner.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Ingredient entity storing nutritional data per standard unit.
 * Each ingredient tracks macronutrients (calories, protein, carbs, fat)
 * and micronutrients (vitamins, minerals) for nutritional analysis.
 * Values are stored per 100 grams of the ingredient.
 */
@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the ingredient, must be unique across the system */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Calorie content per 100g, used for daily intake calculations */
    @Column(nullable = false)
    private Double calories;

    /** Protein content in grams per 100g */
    @Column(nullable = false)
    private Double protein;

    /** Carbohydrate content in grams per 100g */
    @Column(nullable = false)
    private Double carbs;

    /** Fat content in grams per 100g */
    @Column(nullable = false)
    private Double fat;

    /** Dietary fiber in grams per 100g */
    @Column(nullable = false)
    private Double fiber;

    /** Vitamin A content in micrograms per 100g */
    @Column(nullable = false)
    private Double vitaminA;

    /** Vitamin C content in milligrams per 100g */
    @Column(nullable = false)
    private Double vitaminC;

    /** Calcium content in milligrams per 100g */
    @Column(nullable = false)
    private Double calcium;

    /** Iron content in milligrams per 100g */
    @Column(nullable = false)
    private Double iron;

    /** Standard measurement unit (e.g., per 100g, per cup) */
    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
