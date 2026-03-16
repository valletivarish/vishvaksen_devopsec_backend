package com.inventorymanagement.repository;

import com.inventorymanagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    List<Category> findByDeletedFalse();

    Optional<Category> findByNameIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCaseAndDeletedFalse(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    long countByDeletedFalse();
}
