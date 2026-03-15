package com.inventorymanagement.repository;

import com.inventorymanagement.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Warehouse} entity.
 *
 * Provides standard CRUD operations inherited from {@link JpaRepository} along
 * with custom methods for case-insensitive name lookups and existence checks.
 *
 * Warehouses represent physical storage locations. The methods defined here
 * ensure that duplicate warehouse names cannot be created and that warehouses
 * can be resolved by their display name regardless of letter casing.
 *
 * Services that depend on this repository should receive it via constructor
 * injection.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    /**
     * Retrieves a warehouse by its name, performing a case-insensitive comparison.
     *
     * Useful when resolving a warehouse from user input where the exact casing
     * may differ from the stored value.
     *
     * @param name the warehouse name to search for
     * @return an {@link Optional} containing the matching warehouse, or empty if none exists
     */
    Optional<Warehouse> findByNameIgnoreCase(String name);

    /**
     * Checks whether a warehouse with the given name already exists (case-insensitive).
     *
     * Called during warehouse creation to enforce uniqueness at the application
     * layer before attempting a persist that would violate the database constraint.
     *
     * @param name the warehouse name to check
     * @return {@code true} if a warehouse with this name exists, {@code false} otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
