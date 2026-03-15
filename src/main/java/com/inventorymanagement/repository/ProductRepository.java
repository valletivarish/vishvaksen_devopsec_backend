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
    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.reorderLevel")
    List<Product> findByCurrentStockLessThanEqualReorderLevel();

    /**
     * Searches for products whose name contains the given substring,
     * ignoring case differences.
     *
     * Supports the product search/filter feature in the UI, where users
     * type partial names to narrow down results.
     *
     * @param name the search term to match against product names
     * @return a list of products whose names contain the search term
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Counts the number of products that belong to the specified category.
     *
     * Useful for dashboard statistics and validation checks (e.g., preventing
     * deletion of a category that still contains products).
     *
     * @param categoryId the primary key of the target category
     * @return the number of products in the given category
     */
    long countByCategory_Id(Long categoryId);

    /**
     * Counts the number of products supplied by the specified supplier.
     *
     * Useful for dashboard statistics and validation checks (e.g., preventing
     * deletion of a supplier that still has associated products).
     *
     * @param supplierId the primary key of the target supplier
     * @return the number of products from the given supplier
     */
    long countBySupplier_Id(Long supplierId);
}
