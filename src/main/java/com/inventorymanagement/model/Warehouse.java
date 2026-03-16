package com.inventorymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing a physical warehouse (storage facility) within the
 * Inventory Management System.
 *
 * Warehouses are the locations where stock is held. Each {@link StockMovement}
 * record references a warehouse to indicate where goods are received into,
 * dispatched from, or transferred between.
 *
 * The {@code name} field is unique to prevent ambiguous references when
 * recording stock movements.
 */
@Entity
@Table(
    name = "warehouses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouses_name", columnNames = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    /**
     * Primary key -- auto-generated surrogate identifier for the warehouse.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique, human-readable name of the warehouse (e.g., "Main Distribution
     * Center", "West Coast Hub"). Must not be blank and is capped at 200
     * characters.
     */
    @NotBlank(message = "Warehouse name is required")
    @Size(max = 200, message = "Warehouse name must not exceed 200 characters")
    @Column(name = "name", nullable = false, unique = true, length = 200)
    private String name;

    /**
     * Physical address or description of where the warehouse is situated
     * (e.g., "123 Industrial Ave, Chicago, IL 60601"). Must not be blank.
     * Limited to 500 characters.
     */
    @NotBlank(message = "Location is required")
    @Size(max = 500, message = "Location must not exceed 500 characters")
    @Column(name = "location", nullable = false, length = 500)
    private String location;

    /**
     * Maximum storage capacity of the warehouse expressed in abstract units
     * (e.g., pallet slots, shelf positions). Must be at least 1 and at most
     * 1,000,000. Used for capacity-planning reports and alerts.
     */
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 1000000, message = "Capacity must not exceed 1000000")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /**
     * Timestamp recording when the warehouse record was created. Automatically
     * set via the {@link #onCreate()} lifecycle callback.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // -----------------------------------------------------------------------
    // Lifecycle callbacks
    // -----------------------------------------------------------------------

    /**
     * JPA callback that sets the {@code createdAt} timestamp to the current
     * date-time just before the entity is persisted for the first time.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
