package com.mealplanner.api.repository;

import com.mealplanner.api.model.Recipe;
import com.mealplanner.api.model.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Recipe entity.
 * Includes custom queries for searching and filtering recipes
 * by title, difficulty, and user ownership.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /** Find all recipes created by a specific user */
    List<Recipe> findByUserId(Long userId);

    /** Find recipes by difficulty level for filtering */
    List<Recipe> findByDifficulty(Difficulty difficulty);

    /** Search recipes by title containing the search term (case-insensitive) */
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> searchByTitle(@Param("keyword") String keyword);

    /** Find recipes that can be prepared within a given time limit */
    @Query("SELECT r FROM Recipe r WHERE (r.prepTime + r.cookTime) <= :maxMinutes")
    List<Recipe> findByMaxTotalTime(@Param("maxMinutes") int maxMinutes);

    /** Count total recipes per user for dashboard statistics */
    long countByUserId(Long userId);
}
