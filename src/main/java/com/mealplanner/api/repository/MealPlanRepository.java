package com.mealplanner.api.repository;

import com.mealplanner.api.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MealPlan entity with user-scoped queries.
 */
@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    /** Find all meal plans belonging to a specific user */
    List<MealPlan> findByUserId(Long userId);

    /** Count meal plans per user for dashboard statistics */
    long countByUserId(Long userId);
}
