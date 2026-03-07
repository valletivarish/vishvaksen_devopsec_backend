package com.mealplanner.api.repository;

import com.mealplanner.api.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for RecipeIngredient join entity.
 * Provides queries to find ingredient associations for a recipe.
 */
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    /** Find all ingredient entries for a specific recipe */
    List<RecipeIngredient> findByRecipeId(Long recipeId);

    /** Delete all ingredient entries for a recipe (used during recipe update) */
    void deleteByRecipeId(Long recipeId);
}
