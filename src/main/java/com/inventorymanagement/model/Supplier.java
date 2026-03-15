package com.inventorymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a supplier (vendor) that provides products to the
 * Inventory Management System.
 *
 * Suppliers are linked to one or more {@link Product} entries through a
 * one-to-many relationship. Maintaining supplier contact details here enables
 * streamlined procurement workflows and communication.
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "products")
public class Supplier {

    /**
     * Primary key -- auto-generated surrogate identifier for the supplier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Legal or trading name of the supplier (e.g., "Acme Corp").
     * Must not be blank and is limited to 200 characters.
     */
    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name must not exceed 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Primary contact email address for the supplier. Must conform to
     * standard email format for reliable communication.
     */
    @Email(message = "Contact email must be a valid email address")
    @Column(name = "contact_email")
    private String contactEmail;

    /**
     * Contact phone number for the supplier. Accepts digits, spaces, hyphens,
     * parentheses, and an optional leading plus sign to support international
     * formats. Limited to 20 characters.
     */
    @Pattern(
        regexp = "^\\+?[0-9\\s\\-()]{0,20}$",
        message = "Phone number must be valid (digits, spaces, hyphens, parentheses, optional leading +)"
    )
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Physical or mailing address of the supplier. Useful for shipping
     * and invoice reconciliation. Limited to 500 characters.
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    /**
     * Timestamp recording when the supplier record was created. Automatically
     * set via the {@link #onCreate()} lifecycle callback.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Collection of products supplied by this supplier.
     *
     * Mapped by the {@code supplier} field on the {@link Product} entity.
     * Lazy fetching is used to prevent unnecessary loading of the full
     * product list when only supplier metadata is required.
     */
    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
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
