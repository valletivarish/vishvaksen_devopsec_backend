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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        return warehouseRepository.findByDeletedFalse().stream()
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
        if (warehouseRepository.existsByNameIgnoreCaseAndDeletedFalse(warehouseDto.getName())) {
            throw new DuplicateResourceException("Warehouse", "name", warehouseDto.getName());
        }

        // Reactivate a soft-deleted warehouse with the same name instead of inserting
        // a duplicate that would violate the unique constraint.
        Warehouse warehouse = warehouseRepository.findByNameIgnoreCase(warehouseDto.getName())
                .filter(Warehouse::isDeleted)
                .map(existing -> {
                    existing.setName(warehouseDto.getName());
                    existing.setLocation(warehouseDto.getLocation());
                    existing.setCapacity(warehouseDto.getCapacity());
                    existing.setDeleted(false);
                    existing.setDeletedAt(null);
                    return existing;
                })
                .orElseGet(() -> Warehouse.builder()
                        .name(warehouseDto.getName())
                        .location(warehouseDto.getLocation())
                        .capacity(warehouseDto.getCapacity())
                        .build());

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToResponseDto(savedWarehouse);
    }

    @Transactional
    public WarehouseResponseDto updateWarehouse(Long id, WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        warehouseRepository.findByNameIgnoreCaseAndDeletedFalse(warehouseDto.getName())
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
        warehouse.setDeleted(true);
        warehouse.setDeletedAt(LocalDateTime.now());
        warehouseRepository.save(warehouse);
    }

    @Transactional
    public WarehouseResponseDto toggleWarehouseStatus(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouse.setDeleted(!warehouse.isDeleted());
        warehouse.setDeletedAt(warehouse.isDeleted() ? LocalDateTime.now() : null);
        warehouseRepository.save(warehouse);
        return mapToResponseDto(warehouse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> getAllWarehousesIncludingDeleted() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private WarehouseResponseDto mapToResponseDto(Warehouse warehouse) {
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
                .active(!warehouse.isDeleted())
                .createdAt(warehouse.getCreatedAt())
                .build();
    }
}
