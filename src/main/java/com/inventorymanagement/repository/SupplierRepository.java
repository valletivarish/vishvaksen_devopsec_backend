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

    List<Supplier> findByDeletedFalse();

    List<Supplier> findByNameContainingIgnoreCaseAndDeletedFalse(String name);

    List<Supplier> findByNameContainingIgnoreCase(String name);

    boolean existsByContactEmail(String email);

    boolean existsByContactEmailAndDeletedFalse(String email);

    Optional<Supplier> findByContactEmail(String email);

    Optional<Supplier> findByContactEmailAndDeletedFalse(String email);

    long countByDeletedFalse();
}
