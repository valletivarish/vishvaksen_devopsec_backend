package com.inventorymanagement.controller;

import com.inventorymanagement.dto.SupplierDto;
import com.inventorymanagement.dto.SupplierResponseDto;
import com.inventorymanagement.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * REST controller for supplier management operations.
 * Provides CRUD endpoints and search functionality for managing product suppliers.
 */
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Constructor injection for SupplierService dependency.
     *
     * @param supplierService the service handling supplier business logic
     */
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Retrieves all suppliers.
     *
     * @return a list of all suppliers
     */
    @GetMapping
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
        List<SupplierResponseDto> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * Retrieves a single supplier by its unique identifier.
     *
     * @param id the supplier ID
     * @return the matching supplier details
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
        SupplierResponseDto supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    /**
     * Registers a new supplier in the system.
     *
     * @param supplierDto the supplier details to create
     * @return the created supplier with generated ID
     */
    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        SupplierResponseDto createdSupplier = supplierService.createSupplier(supplierDto);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    /**
     * Updates an existing supplier with new details.
     *
     * @param id          the ID of the supplier to update
     * @param supplierDto the updated supplier details
     * @return the updated supplier
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(@PathVariable Long id,
                                                              @Valid @RequestBody SupplierDto supplierDto) {
        SupplierResponseDto updatedSupplier = supplierService.updateSupplier(id, supplierDto);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * Deletes a supplier by its ID.
     * Products associated with this supplier should be reassigned or handled before deletion.
     *
     * @param id the ID of the supplier to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for suppliers whose name matches the given query string.
     *
     * @param name the search term to match against supplier names
     * @return a list of suppliers matching the search criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponseDto>> searchSuppliers(@RequestParam String name) {
        List<SupplierResponseDto> suppliers = supplierService.searchSuppliers(name);
        return ResponseEntity.ok(suppliers);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<SupplierResponseDto> toggleSupplierStatus(@PathVariable Long id) {
        SupplierResponseDto supplier = supplierService.toggleSupplierStatus(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliersIncludingDeleted() {
        List<SupplierResponseDto> suppliers = supplierService.getAllSuppliersIncludingDeleted();
        return ResponseEntity.ok(suppliers);
    }
}
