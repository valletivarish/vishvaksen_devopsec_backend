package com.inventorymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing a single stock movement event in the Inventory
 * Management System.
 *
 * A stock movement records the transfer of a specific quantity of a
 * {@link Product} into, out of, or between {@link Warehouse} locations.
 * Together, these records form an auditable ledger of all inventory changes.
 *
 * The {@link MovementType} discriminator indicates whether stock is being
 * received (IN), dispatched (OUT), or relocated (TRANSFER).
 *
 * The optional {@code referenceNumber} field can hold external document
 * identifiers such as purchase-order numbers, sales-order numbers, or
 * internal transfer request IDs for cross-system traceability.
 */
@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "warehouse"})
public class StockMovement {

    /**
     * Primary key -- auto-generated surrogate identifier for the movement record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The product whose stock quantity is affected by this movement.
     * Establishes a many-to-one relationship with {@link Product}.
     * Must not be null -- every movement must reference a valid product.
     */
    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "product_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_stock_movements_product")
    )
    private Product product;

    /**
     * The warehouse where the stock movement takes place. For IN movements
     * this is the receiving warehouse; for OUT movements it is the dispatching
     * warehouse; for TRANSFER movements it represents the source (a
     * complementary IN record would reference the destination).
     */
    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "warehouse_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_stock_movements_warehouse")
    )
    private Warehouse warehouse;

    /**
     * Number of units involved in this movement. Must be at least 1 (a
     * movement of zero units is meaningless) and at most 100,000 to guard
     * against data-entry errors.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100000, message = "Quantity must not exceed 100000")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Discriminator indicating the direction of the stock movement.
     * Stored as a string in the database for clarity in raw queries.
     *
     * @see MovementType
     */
    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private MovementType type;

    /**
     * Optional external reference number linking this movement to a source
     * document (e.g., "PO-2026-00451", "SO-78432"). Useful for reconciliation
     * with procurement or sales systems. Limited to 100 characters.
     */
    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    /**
     * Optional free-text notes providing additional context about the movement
     * (e.g., "Damaged goods returned by customer", "Emergency restock").
     * Limited to 500 characters.
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * The date and time when the physical stock movement actually occurred.
     * This may differ from {@code createdAt} if movements are recorded
     * retroactively.
     */
    @Column(name = "movement_date")
    private LocalDateTime movementDate;

    /**
     * Timestamp recording when this movement record was persisted in the
     * database. Automatically set via the {@link #onCreate()} lifecycle
     * callback.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // -----------------------------------------------------------------------
    // Lifecycle callbacks
    // -----------------------------------------------------------------------

    /**
     * JPA callback executed before the initial persist. Sets {@code createdAt}
     * to the current date-time. If no explicit {@code movementDate} has been
     * provided, it defaults to the current date-time as well.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.movementDate == null) {
            this.movementDate = this.createdAt;
        }
    }
}
