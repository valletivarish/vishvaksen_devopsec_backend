package com.inventorymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a product category in the Inventory Management System.
 *
 * Categories provide a logical grouping mechanism for products, making it easier
 * to browse, filter, and report on inventory. Each category may contain zero or
 * more {@link Product} entries via a one-to-many relationship.
 *
 * The {@code name} field is unique so that duplicate category labels cannot exist.
 */
@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_categories_name", columnNames = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "products")
public class Category {

    /**
     * Primary key -- auto-generated surrogate identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable name of the category (e.g., "Electronics", "Furniture").
     * Must be unique and non-blank, with a maximum length of 100 characters.
     */
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Optional free-text description providing additional context about the
     * category. Limited to 500 characters.
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Timestamp recording when the category was created. Automatically set
     * via the {@link #onCreate()} lifecycle callback.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Collection of products that belong to this category.
     *
     * Mapped by the {@code category} field on the {@link Product} entity.
     * Cascading is intentionally omitted so that product lifecycle management
     * remains explicit. Lazy fetching avoids loading the full product graph
     * when only the category metadata is needed.
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

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
