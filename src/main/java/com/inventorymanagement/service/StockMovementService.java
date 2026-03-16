package com.inventorymanagement.service;

import com.inventorymanagement.dto.StockMovementDto;
import com.inventorymanagement.dto.StockMovementResponseDto;
import com.inventorymanagement.exception.InsufficientStockException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.exception.WarehouseCapacityExceededException;
import com.inventorymanagement.model.MovementType;
import com.inventorymanagement.model.Product;
import com.inventorymanagement.model.StockMovement;
import com.inventorymanagement.model.Warehouse;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.StockMovementRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for stock movement operations.
 *
 * Stock movements form the core of the inventory ledger. Every change to
 * product quantity -- whether receiving goods (IN), dispatching goods (OUT),
 * or relocating goods between warehouses (TRANSFER) -- is recorded as a
 * movement. This service ensures data integrity by validating that sufficient
 * stock exists for outbound movements and by atomically updating the
 * product's denormalized currentStock counter within the same transaction.
 */
@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Constructor injection of repositories needed for stock movement operations.
     *
     * @param stockMovementRepository repository for movement persistence and queries
     * @param productRepository       repository for validating products and updating stock levels
     * @param warehouseRepository     repository for validating warehouse references
     */
    public StockMovementService(StockMovementRepository stockMovementRepository,
                                ProductRepository productRepository,
                                WarehouseRepository warehouseRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Retrieves all stock movements in the system.
     *
     * Each movement is mapped to a response DTO that includes resolved
     * product and warehouse names for client-side display.
     *
     * @return a list of all stock movements as response DTOs
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDto> getAllMovements() {
        return stockMovementRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single stock movement by its primary key.
     *
     * @param id the movement ID to look up
     * @return the stock movement as a response DTO
     * @throws ResourceNotFoundException if no movement exists with the given ID
     */
    @Transactional(readOnly = true)
    public StockMovementResponseDto getMovementById(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));
        return mapToResponseDto(movement);
    }

    /**
     * Creates a new stock movement and updates product stock levels accordingly.
     *
     * Business rules enforced:
     * 1. The referenced product must exist in the database.
     * 2. The referenced warehouse must exist in the database.
     * 3. For OUT movements, the product's current stock must be greater than or
     *    equal to the requested quantity. This prevents negative inventory, which
     *    would indicate a data integrity issue.
     * 4. For IN movements, the product's currentStock is increased by the quantity.
     * 5. For OUT movements, the product's currentStock is decreased by the quantity.
     * 6. For TRANSFER movements, no stock change is applied -- transfers between
     *    warehouses are recorded for auditing purposes only, as the net effect
     *    on the global product stock level is zero.
     * 7. The movement date is set to the current timestamp.
     *
     * All operations (movement creation + stock update) occur within a single
     * transaction to guarantee atomicity.
     *
     * @param dto the stock movement creation payload
     * @return the newly created stock movement as a response DTO
     * @throws ResourceNotFoundException  if the product or warehouse does not exist
     * @throws InsufficientStockException if an OUT movement exceeds available stock
     */
    @Transactional
    public StockMovementResponseDto createMovement(StockMovementDto dto) {
        // Validate that the referenced product exists
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.getProductId()));

        // Validate that the referenced warehouse exists
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", dto.getWarehouseId()));

        // The DTO now uses the same MovementType enum as the model
        MovementType movementType = dto.getType();

        // For outbound movements, verify sufficient stock before proceeding
        if (movementType == MovementType.OUT) {
            if (product.getCurrentStock() < dto.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(), dto.getQuantity(), product.getCurrentStock());
            }
        }

        // For inbound movements, verify warehouse has enough remaining capacity
        if (movementType == MovementType.IN) {
            int currentUtilization = calculateWarehouseUtilization(warehouse.getId());
            if (currentUtilization + dto.getQuantity() > warehouse.getCapacity()) {
                throw new WarehouseCapacityExceededException(
                        warehouse.getName(), dto.getQuantity(), currentUtilization, warehouse.getCapacity());
            }
        }

        // Build and persist the stock movement record with current timestamp
        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(dto.getQuantity())
                .type(movementType)
                .referenceNumber(dto.getReferenceNumber())
                .notes(dto.getNotes())
                .movementDate(LocalDateTime.now())
                .build();

        StockMovement savedMovement = stockMovementRepository.save(movement);

        // Update the product's denormalized stock counter based on movement direction.
        // IN movements add stock; OUT movements subtract stock; TRANSFER movements
        // have no net effect on the global product stock level.
        if (movementType == MovementType.IN) {
            product.setCurrentStock(product.getCurrentStock() + dto.getQuantity());
            productRepository.save(product);
        } else if (movementType == MovementType.OUT) {
            product.setCurrentStock(product.getCurrentStock() - dto.getQuantity());
            productRepository.save(product);
        }
        // TRANSFER: no stock change -- the product remains in the system,
        // just at a different warehouse location. The transfer is recorded
        // purely for audit and traceability purposes.

        return mapToResponseDto(savedMovement);
    }

    /**
     * Retrieves all stock movements associated with a specific product.
     *
     * Useful for viewing the complete movement history of a product, such as
     * when diagnosing stock discrepancies or auditing procurement activity.
     *
     * @param productId the ID of the product to filter by
     * @return a list of stock movements for the given product
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDto> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProduct_Id(productId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all stock movements associated with a specific warehouse.
     *
     * Useful for warehouse-level activity reports and capacity monitoring.
     *
     * @param warehouseId the ID of the warehouse to filter by
     * @return a list of stock movements for the given warehouse
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDto> getMovementsByWarehouse(Long warehouseId) {
        return stockMovementRepository.findByWarehouse_Id(warehouseId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all stock movements of the specified type.
     *
     * Allows the UI to filter movements by direction (IN, OUT, or TRANSFER)
     * for focused reporting and analysis.
     *
     * @param type the movement type to filter by
     * @return a list of stock movements matching the given type
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDto> getMovementsByType(MovementType type) {
        return stockMovementRepository.findByType(type).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the 10 most recently created stock movements.
     *
     * Designed to power the "recent activity" widget on the dashboard,
     * giving users a quick overview of the latest inventory changes
     * without loading the entire movement history.
     *
     * @return a list of up to 10 stock movements, most recent first
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDto> getRecentMovements() {
        return stockMovementRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a StockMovement entity to a StockMovementResponseDto.
     *
     * Resolves product and warehouse names from the lazy-loaded associations
     * so the client receives a fully denormalized view. The movement type is
     * mapped from the model enum to the DTO enum for API consistency.
     *
     * @param movement the stock movement entity to convert
     * @return the fully populated response DTO
     */
    private int calculateWarehouseUtilization(Long warehouseId) {
        List<StockMovement> movements = stockMovementRepository.findByWarehouse_Id(warehouseId);

        int totalIn = movements.stream()
                .filter(m -> m.getType() == MovementType.IN)
                .mapToInt(StockMovement::getQuantity)
                .sum();

        int totalOut = movements.stream()
                .filter(m -> m.getType() == MovementType.OUT)
                .mapToInt(StockMovement::getQuantity)
                .sum();

        return Math.max(totalIn - totalOut, 0);
    }

    private StockMovementResponseDto mapToResponseDto(StockMovement movement) {
        return StockMovementResponseDto.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .warehouseId(movement.getWarehouse().getId())
                .warehouseName(movement.getWarehouse().getName())
                .quantity(movement.getQuantity())
                .type(movement.getType())
                .referenceNumber(movement.getReferenceNumber())
                .notes(movement.getNotes())
                .movementDate(movement.getMovementDate())
                .build();
    }
}
