package com.inventorymanagement.repository;

import com.inventorymanagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Category} entity.
 *
 * Provides standard CRUD operations inherited from {@link JpaRepository} along
 * with custom methods for case-insensitive name lookups and existence checks.
 *
 * Categories serve as a logical grouping mechanism for products. The methods
 * defined here ensure that duplicate category names cannot be created and that
 * categories can be resolved by their display name regardless of letter casing.
 *
 * Services that depend on this repository should receive it via constructor
 * injection.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Retrieves a category by its name, performing a case-insensitive comparison.
     *
     * Useful when resolving a category from user input where the exact casing
     * may differ from the stored value.
     *
     * @param name the category name to search for
     * @return an {@link Optional} containing the matching category, or empty if none exists
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Checks whether a category with the given name already exists (case-insensitive).
     *
     * Called during category creation to enforce uniqueness at the application
     * layer before attempting a persist that would violate the database constraint.
     *
     * @param name the category name to check
     * @return {@code true} if a category with this name exists, {@code false} otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
