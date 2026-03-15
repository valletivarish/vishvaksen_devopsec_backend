package com.inventorymanagement.service;

import com.inventorymanagement.dto.WarehouseDto;
import com.inventorymanagement.dto.WarehouseResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.MovementType;
import com.inventorymanagement.model.StockMovement;
import com.inventorymanagement.model.Warehouse;
import com.inventorymanagement.repository.StockMovementRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for warehouse management operations.
 *
 * Handles CRUD operations for warehouses, enforcing business rules
 * such as unique warehouse names.
 */
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StockMovementRepository stockMovementRepository;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            StockMovementRepository stockMovementRepository) {
        this.warehouseRepository = warehouseRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDto getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return mapToResponseDto(warehouse);
    }

    @Transactional
    public WarehouseResponseDto createWarehouse(WarehouseDto warehouseDto) {
        if (warehouseRepository.existsByNameIgnoreCase(warehouseDto.getName())) {
            throw new DuplicateResourceException("Warehouse", "name", warehouseDto.getName());
        }

        Warehouse warehouse = Warehouse.builder()
                .name(warehouseDto.getName())
                .location(warehouseDto.getLocation())
                .capacity(warehouseDto.getCapacity())
                .build();

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToResponseDto(savedWarehouse);
    }

    /**
     * Updates an existing warehouse.
     *
     * Name uniqueness is re-validated excluding the current warehouse so that
     * saving without changing the name does not trigger a false duplicate error.
     *
     * @param id           the warehouse ID to update
     * @param warehouseDto the warehouse update payload
     * @return the updated warehouse as a response DTO
     * @throws ResourceNotFoundException  if no warehouse exists with the given ID
     * @throws DuplicateResourceException if another warehouse already uses the new name
     */
    @Transactional
    public WarehouseResponseDto updateWarehouse(Long id, WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        // Check name uniqueness only if the name is being changed to a different value
        warehouseRepository.findByNameIgnoreCase(warehouseDto.getName())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new DuplicateResourceException("Warehouse", "name", warehouseDto.getName());
                    }
                });

        warehouse.setName(warehouseDto.getName());
        warehouse.setLocation(warehouseDto.getLocation());
        warehouse.setCapacity(warehouseDto.getCapacity());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return mapToResponseDto(updatedWarehouse);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouseRepository.delete(warehouse);
    }

    /**
     * Maps a Warehouse entity to a WarehouseResponseDto.
     *
     * The currentUtilization is calculated by summing all IN movements for
     * this warehouse and subtracting all OUT movements. This gives a net
     * figure representing how many stock units are currently stored at the
     * warehouse. TRANSFER movements are excluded from this calculation
     * because they are recorded as separate IN/OUT pairs per warehouse.
     *
     * @param warehouse the warehouse entity to convert
     * @return the fully populated response DTO including utilization data
     */
    private WarehouseResponseDto mapToResponseDto(Warehouse warehouse) {
        // Calculate net stock held at this warehouse: total IN minus total OUT
        List<StockMovement> warehouseMovements = stockMovementRepository.findByWarehouse_Id(warehouse.getId());

        int totalIn = warehouseMovements.stream()
                .filter(m -> m.getType() == MovementType.IN)
                .mapToInt(StockMovement::getQuantity)
                .sum();

        int totalOut = warehouseMovements.stream()
                .filter(m -> m.getType() == MovementType.OUT)
                .mapToInt(StockMovement::getQuantity)
                .sum();

        int currentUtilization = totalIn - totalOut;

        return WarehouseResponseDto.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .capacity(warehouse.getCapacity())
                .currentUtilization(currentUtilization)
                .createdAt(warehouse.getCreatedAt())
                .build();
    }
}
