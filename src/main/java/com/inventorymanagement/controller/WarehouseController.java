package com.inventorymanagement.controller;

import com.inventorymanagement.dto.WarehouseDto;
import com.inventorymanagement.dto.WarehouseResponseDto;
import com.inventorymanagement.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * REST controller for warehouse management operations.
 * Provides CRUD endpoints for managing storage locations in the inventory system.
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * Constructor injection for WarehouseService dependency.
     *
     * @param warehouseService the service handling warehouse business logic
     */
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * Retrieves all warehouses.
     *
     * @return a list of all warehouses
     */
    @GetMapping
    public ResponseEntity<List<WarehouseResponseDto>> getAllWarehouses() {
        List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    /**
     * Retrieves a single warehouse by its unique identifier.
     *
     * @param id the warehouse ID
     * @return the matching warehouse details
     */
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDto> getWarehouseById(@PathVariable Long id) {
        WarehouseResponseDto warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(warehouse);
    }

    /**
     * Creates a new warehouse in the system.
     *
     * @param warehouseDto the warehouse details to create
     * @return the created warehouse with generated ID
     */
    @PostMapping
    public ResponseEntity<WarehouseResponseDto> createWarehouse(@Valid @RequestBody WarehouseDto warehouseDto) {
        WarehouseResponseDto createdWarehouse = warehouseService.createWarehouse(warehouseDto);
        return new ResponseEntity<>(createdWarehouse, HttpStatus.CREATED);
    }

    /**
     * Updates an existing warehouse with new details.
     *
     * @param id           the ID of the warehouse to update
     * @param warehouseDto the updated warehouse details
     * @return the updated warehouse
     */
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDto> updateWarehouse(@PathVariable Long id,
                                                                @Valid @RequestBody WarehouseDto warehouseDto) {
        WarehouseResponseDto updatedWarehouse = warehouseService.updateWarehouse(id, warehouseDto);
        return ResponseEntity.ok(updatedWarehouse);
    }

    /**
     * Deletes a warehouse by its ID.
     * Stock movements and inventory associated with this warehouse should be handled before deletion.
     *
     * @param id the ID of the warehouse to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<WarehouseResponseDto> toggleWarehouseStatus(@PathVariable Long id) {
        WarehouseResponseDto warehouse = warehouseService.toggleWarehouseStatus(id);
        return ResponseEntity.ok(warehouse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<WarehouseResponseDto>> getAllWarehousesIncludingDeleted() {
        List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehousesIncludingDeleted();
        return ResponseEntity.ok(warehouses);
    }
}
