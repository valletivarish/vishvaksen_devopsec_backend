package com.mealplanner.api.repository;

import com.mealplanner.api.model.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ShoppingListItem entity for managing individual items.
 */
@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    /** Find all items in a specific shopping list */
    List<ShoppingListItem> findByShoppingListId(Long shoppingListId);
}
