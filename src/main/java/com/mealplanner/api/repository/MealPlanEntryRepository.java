package com.mealplanner.api.repository;

import com.mealplanner.api.model.MealPlanEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MealPlanEntry entity linking recipes to meal plan slots.
 */
@Repository
public interface MealPlanEntryRepository extends JpaRepository<MealPlanEntry, Long> {

    /** Find all entries for a specific meal plan */
    List<MealPlanEntry> findByMealPlanId(Long mealPlanId);

    /** Delete all entries for a meal plan (used during update) */
    void deleteByMealPlanId(Long mealPlanId);
}
