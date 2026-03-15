package com.inventorymanagement.repository;

import com.inventorymanagement.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Supplier} entity.
 *
 * Provides standard CRUD operations inherited from {@link JpaRepository} along
 * with custom methods for searching suppliers by name, looking up suppliers by
 * their contact email, and verifying email uniqueness.
 *
 * Services that depend on this repository should receive it via constructor
 * injection.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Searches for suppliers whose name contains the given substring,
     * ignoring case differences.
     *
     * Supports the supplier search/filter feature in the UI, where users
     * type partial names to narrow down the supplier list.
     *
     * @param name the search term to match against supplier names
     * @return a list of suppliers whose names contain the search term, possibly empty
     */
    List<Supplier> findByNameContainingIgnoreCase(String name);

    /**
     * Checks whether a supplier with the given contact email already exists.
     *
     * Used during supplier creation or update to prevent duplicate email
     * entries across supplier records.
     *
     * @param email the contact email address to check
     * @return {@code true} if a supplier with this email exists, {@code false} otherwise
     */
    boolean existsByContactEmail(String email);

    /**
     * Retrieves a supplier by their contact email address.
     *
     * Useful for resolving a supplier from inbound communication (e.g.,
     * matching an incoming email to the correct supplier record).
     *
     * @param email the exact contact email address to search for
     * @return an {@link Optional} containing the matching supplier, or empty if none exists
     */
    Optional<Supplier> findByContactEmail(String email);
}
