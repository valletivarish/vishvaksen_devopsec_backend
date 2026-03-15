package com.inventorymanagement.repository;

import com.inventorymanagement.model.MovementType;
import com.inventorymanagement.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link StockMovement} entity.
 *
 * Provides standard CRUD operations inherited from {@link JpaRepository} along
 * with custom query methods that support filtering by product, warehouse,
 * movement type, and date range. Additional queries are included for dashboard
 * features such as recent-movement feeds and aggregate stock calculations.
 *
 * Services that depend on this repository should receive it via constructor
 * injection.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /**
     * Finds all stock movements associated with the specified product.
     *
     * Uses Spring Data's property-traversal syntax to navigate the
     * {@code product} association and match on its {@code id} field.
     *
     * @param productId the primary key of the target product
     * @return a list of stock movements for the given product, possibly empty
     */
    List<StockMovement> findByProduct_Id(Long productId);

    /**
     * Finds all stock movements associated with the specified warehouse.
     *
     * Uses Spring Data's property-traversal syntax to navigate the
     * {@code warehouse} association and match on its {@code id} field.
     *
     * @param warehouseId the primary key of the target warehouse
     * @return a list of stock movements for the given warehouse, possibly empty
     */
    List<StockMovement> findByWarehouse_Id(Long warehouseId);

    /**
     * Finds all stock movements of the specified type.
     *
     * Allows filtering the movement history by direction (IN, OUT, or TRANSFER),
     * which is useful for generating type-specific reports.
     *
     * @param type the {@link MovementType} to filter by
     * @return a list of stock movements matching the given type, possibly empty
     */
    List<StockMovement> findByType(MovementType type);

    /**
     * Finds all stock movements whose movement date falls within the specified
     * date-time range (inclusive on both ends).
     *
     * Supports date-range filtering in reports and audit views, enabling users
     * to narrow results to a particular time window.
     *
     * @param start the start of the date-time range (inclusive)
     * @param end   the end of the date-time range (inclusive)
     * @return a list of stock movements within the date range, possibly empty
     */
    List<StockMovement> findByMovementDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves the ten most recently created stock movements, ordered by
     * creation timestamp in descending order.
     *
     * Designed to power the "recent movements" widget on the dashboard,
     * giving users a quick overview of the latest inventory activity.
     *
     * @return a list of up to 10 stock movements, most recent first
     */
    List<StockMovement> findTop10ByOrderByCreatedAtDesc();

    /**
     * Calculates the total quantity of stock movements for a given product
     * and movement type.
     *
     * This aggregate query is essential for stock-level calculations. For example,
     * the net available stock of a product can be derived by summing all IN
     * movements and subtracting all OUT movements. TRANSFER movements can be
     * handled separately depending on the warehouse context.
     *
     * @param productId the primary key of the target product
     * @param type      the {@link MovementType} to aggregate (IN, OUT, or TRANSFER)
     * @return the total quantity for the given product and movement type, or
     *         {@code null} if no matching movements exist
     */
    @Query("SELECT SUM(sm.quantity) FROM StockMovement sm " +
           "WHERE sm.product.id = :productId AND sm.type = :type")
    Long sumQuantityByProductIdAndType(@Param("productId") Long productId,
                                       @Param("type") MovementType type);
}
