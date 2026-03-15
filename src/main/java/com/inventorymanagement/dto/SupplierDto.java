package com.inventorymanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a supplier.
 * Suppliers are external vendors from whom products are sourced.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {

    /** Supplier company or individual name. */
    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name must not exceed 200 characters")
    private String name;

    /** Primary contact email for orders and communication. */
    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    /**
     * Phone number in international or local format.
     * Accepts an optional leading '+', digits, spaces, and hyphens.
     * Length between 7 and 20 characters covers most national and international formats.
     */
    @Pattern(regexp = "^[+]?[0-9\\s-]{7,20}$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    /** Optional physical or mailing address of the supplier. */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
}
