package com.mealplanner.api.repository;

import com.mealplanner.api.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Ingredient entity.
 * Provides queries for ingredient lookup by name and nutritional filtering.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /** Find ingredient by exact name for duplicate checking */
    Optional<Ingredient> findByName(String name);

    /** Check if an ingredient with the given name already exists */
    boolean existsByName(String name);

    /** Search ingredients by name containing keyword (case-insensitive) */
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ingredient> searchByName(@Param("keyword") String keyword);
}
