package com.inventorymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity representing a product (inventory item) in the Inventory
 * Management System.
 *
 * A product belongs to exactly one {@link Category} and is sourced from
 * exactly one {@link Supplier}. The {@code sku} (Stock Keeping Unit) field
 * serves as the business-unique identifier used in barcodes, purchase orders,
 * and stock-movement records.
 *
 * The {@code currentStock} field is a denormalized running total of available
 * units. It is updated whenever a {@link StockMovement} is recorded, giving
 * fast read access without aggregating movement history on every query.
 */
@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_sku", columnNames = "sku")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"category", "supplier"})
public class Product {

    /**
     * Primary key -- auto-generated surrogate identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable product name (e.g., "Wireless Keyboard").
     * Must not be blank and is limited to 200 characters.
     */
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Stock Keeping Unit -- a unique alphanumeric code (hyphens allowed) that
     * identifies the product across procurement and warehouse systems.
     * Examples: "KB-WIRELESS-001", "FURN-DESK-42".
     */
    @NotBlank(message = "SKU is required")
    @Pattern(
        regexp = "^[A-Za-z0-9\\-]+$",
        message = "SKU must contain only alphanumeric characters and hyphens"
    )
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    /**
     * Optional detailed description of the product, covering specifications,
     * use-cases, or any relevant notes. Limited to 1000 characters.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Per-unit price of the product expressed in the system's base currency.
     * Must be between 0.01 and 999999.99 to prevent nonsensical entries.
     * Stored with a precision of 10 digits and 2 decimal places.
     */
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Unit price must not exceed 999999.99")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Minimum stock threshold below which the system should trigger a reorder
     * alert. A value of 0 means reorder alerts are effectively disabled for
     * this product. Maximum allowed value is 100,000.
     */
    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level must be at least 0")
    @Max(value = 100000, message = "Reorder level must not exceed 100000")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    /**
     * Denormalized count of units currently available across all warehouses.
     * Updated programmatically when stock movements are recorded. Must never
     * be negative.
     */
    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock must be at least 0")
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    /**
     * The category to which this product belongs. Establishes a many-to-one
     * relationship with {@link Category}. Loaded lazily to avoid pulling the
     * full category graph on every product fetch.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_products_category"))
    private Category category;

    /**
     * The supplier that provides this product. Establishes a many-to-one
     * relationship with {@link Supplier}. Loaded lazily for performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", foreignKey = @ForeignKey(name = "fk_products_supplier"))
    private Supplier supplier;

    /**
     * Timestamp recording when the product record was first created.
     * Automatically set via the {@link #onCreate()} lifecycle callback.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp recording the most recent update to the product record.
     * Automatically refreshed via the {@link #onUpdate()} lifecycle callback
     * on every merge/update operation.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -----------------------------------------------------------------------
    // Lifecycle callbacks
    // -----------------------------------------------------------------------

    /**
     * JPA callback executed before the initial persist. Sets both
     * {@code createdAt} and {@code updatedAt} to the current date-time.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * JPA callback executed before every subsequent update. Refreshes the
     * {@code updatedAt} timestamp to reflect the latest modification time.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
