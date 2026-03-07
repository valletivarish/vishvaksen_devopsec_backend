package com.mealplanner.api.repository;

import com.mealplanner.api.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ShoppingList entity with user-scoped queries.
 */
@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    /** Find all shopping lists belonging to a specific user */
    List<ShoppingList> findByUserId(Long userId);
}
