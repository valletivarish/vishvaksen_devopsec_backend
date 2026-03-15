package com.inventorymanagement.controller;

import com.inventorymanagement.dto.StockMovementDto;
import com.inventorymanagement.dto.StockMovementResponseDto;
import com.inventorymanagement.model.MovementType;
import com.inventorymanagement.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for stock movement operations.
 * Tracks inventory movements such as incoming stock, outgoing shipments, and transfers between warehouses.
 */
@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    /**
     * Constructor injection for StockMovementService dependency.
     *
     * @param stockMovementService the service handling stock movement business logic
     */
    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    /**
     * Retrieves all stock movements recorded in the system.
     *
     * @return a list of all stock movements
     */
    @GetMapping
    public ResponseEntity<List<StockMovementResponseDto>> getAllMovements() {
        List<StockMovementResponseDto> movements = stockMovementService.getAllMovements();
        return ResponseEntity.ok(movements);
    }

    /**
     * Retrieves a single stock movement by its unique identifier.
     *
     * @param id the stock movement ID
     * @return the matching stock movement details
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponseDto> getMovementById(@PathVariable Long id) {
        StockMovementResponseDto movement = stockMovementService.getMovementById(id);
        return ResponseEntity.ok(movement);
    }

    /**
     * Records a new stock movement in the system.
     * This will also update the product's current stock level accordingly.
     *
     * @param stockMovementDto the stock movement details to record
     * @return the created stock movement with generated ID
     */
    @PostMapping
    public ResponseEntity<StockMovementResponseDto> createMovement(@Valid @RequestBody StockMovementDto stockMovementDto) {
        StockMovementResponseDto createdMovement = stockMovementService.createMovement(stockMovementDto);
        return new ResponseEntity<>(createdMovement, HttpStatus.CREATED);
    }

    /**
     * Retrieves all stock movements associated with a specific product.
     *
     * @param productId the product ID to filter movements by
     * @return a list of stock movements for the given product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByProduct(@PathVariable Long productId) {
        List<StockMovementResponseDto> movements = stockMovementService.getMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }

    /**
     * Retrieves all stock movements associated with a specific warehouse.
     *
     * @param warehouseId the warehouse ID to filter movements by
     * @return a list of stock movements for the given warehouse
     */
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByWarehouse(@PathVariable Long warehouseId) {
        List<StockMovementResponseDto> movements = stockMovementService.getMovementsByWarehouse(warehouseId);
        return ResponseEntity.ok(movements);
    }

    /**
     * Retrieves all stock movements of a specific type (e.g., IN, OUT, TRANSFER).
     *
     * @param type the movement type to filter by
     * @return a list of stock movements matching the given type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<StockMovementResponseDto>> getMovementsByType(@PathVariable MovementType type) {
        List<StockMovementResponseDto> movements = stockMovementService.getMovementsByType(type);
        return ResponseEntity.ok(movements);
    }

    /**
     * Retrieves the most recent stock movements.
     * Useful for dashboard displays and activity monitoring.
     *
     * @return a list of recently recorded stock movements
     */
    @GetMapping("/recent")
    public ResponseEntity<List<StockMovementResponseDto>> getRecentMovements() {
        List<StockMovementResponseDto> movements = stockMovementService.getRecentMovements();
        return ResponseEntity.ok(movements);
    }
}
