package com.inventorymanagement.repository;

import com.inventorymanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Product} entity.
 *
 * Provides standard CRUD operations inherited from {@link JpaRepository} along
 * with custom query methods that support category/supplier filtering, SKU lookup,
 * low-stock detection, and search-by-name functionality.
 *
 * Services that depend on this repository should receive it via constructor
 * injection. Spring generates the implementation at runtime based on method
 * naming conventions and any explicit {@link Query} annotations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products that belong to the specified category.
     *
     * Uses Spring Data's property-traversal syntax to navigate the
     * {@code category} association and match on its {@code id} field.
     *
     * @param categoryId the primary key of the target category
     * @return a list of products in the given category, possibly empty
     */
    List<Product> findByCategory_Id(Long categoryId);

    /**
     * Finds all products supplied by the specified supplier.
     *
     * Uses Spring Data's property-traversal syntax to navigate the
     * {@code supplier} association and match on its {@code id} field.
     *
     * @param supplierId the primary key of the target supplier
     * @return a list of products from the given supplier, possibly empty
     */
    List<Product> findBySupplier_Id(Long supplierId);

    /**
     * Retrieves a product by its SKU, performing a case-insensitive comparison.
     *
     * SKUs are the business-unique identifiers for products; this method
     * supports lookups where the caller may not know the exact casing stored
     * in the database.
     *
     * @param sku the Stock Keeping Unit code to search for
     * @return an {@link Optional} containing the matching product, or empty if none exists
     */
    Optional<Product> findBySkuIgnoreCase(String sku);

    /**
     * Checks whether a product with the given SKU already exists (case-insensitive).
     *
     * Used during product creation to prevent duplicate SKU entries before
     * attempting a persist that would violate the unique constraint.
     *
     * @param sku the SKU to check
     * @return {@code true} if a product with this SKU exists, {@code false} otherwise
     */
    boolean existsBySkuIgnoreCase(String sku);

    /**
     * Finds all products whose current stock has fallen to or below their
     * configured reorder level.
     *
     * This custom JPQL query supports the low-stock alert feature, allowing
     * warehouse managers to identify items that need to be reordered.
     *
     * @return a list of products that are at or below their reorder threshold
     */
    List<Product> findByDeletedFalse();

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.reorderLevel AND p.deleted = false")
    List<Product> findByCurrentStockLessThanEqualReorderLevel();

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.reorderLevel")
    List<Product> findAllByCurrentStockLessThanEqualReorderLevel();

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String name);

    List<Product> findByCategory_IdAndDeletedFalse(Long categoryId);

    List<Product> findBySupplier_IdAndDeletedFalse(Long supplierId);

    long countByCategory_Id(Long categoryId);

    long countByCategory_IdAndDeletedFalse(Long categoryId);

    long countBySupplier_Id(Long supplierId);

    long countBySupplier_IdAndDeletedFalse(Long supplierId);

    long countByDeletedFalse();
}
