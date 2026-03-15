package com.inventorymanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a product.
 * All business-rule constraints are enforced via Jakarta Bean Validation
 * so the service layer can assume valid data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    /** Product display name. Limited to 200 characters to match database column size. */
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    /**
     * Stock Keeping Unit -- a unique alphanumeric identifier with optional hyphens.
     * Pattern ensures no special characters that could break barcode or label systems.
     */
    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "SKU must be alphanumeric with hyphens only")
    private String sku;

    /** Optional long-form description. Capped at 1000 characters for storage efficiency. */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Price per unit in the base currency.
     * Must be at least 0.01 (no free products) and at most 999999.99 to stay within
     * reasonable financial bounds and database precision (DECIMAL(8,2)).
     */
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Unit price must not exceed 999999.99")
    private BigDecimal unitPrice;

    /**
     * Minimum stock threshold that triggers a low-stock alert.
     * Zero means alerts are effectively disabled for this product.
     */
    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level must be at least 0")
    @Max(value = 100000, message = "Reorder level must not exceed 100000")
    private Integer reorderLevel;

    /** Foreign key reference to the product's category. Must exist in the categories table. */
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    /** Foreign key reference to the product's primary supplier. Must exist in the suppliers table. */
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
}
