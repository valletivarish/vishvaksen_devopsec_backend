package com.inventorymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a product category.
 * Categories group products for easier browsing and reporting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    /** Category display name. Must be unique across the system. */
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    /** Optional description explaining the scope of products in this category. */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
