package com.inventorymanagement.service;

import com.inventorymanagement.dto.WarehouseDto;
import com.inventorymanagement.dto.WarehouseResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Warehouse;
import com.inventorymanagement.repository.StockMovementRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WarehouseService}.
 *
 * Verifies warehouse CRUD operations, name uniqueness enforcement, and
 * correct DTO mapping.
 */
@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse warehouse;
    private WarehouseDto warehouseDto;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder()
                .id(1L)
                .name("Main Distribution Center")
                .location("123 Industrial Ave, Chicago, IL")
                .capacity(10000)
                .createdAt(LocalDateTime.now())
                .build();

        warehouseDto = new WarehouseDto("Main Distribution Center", "123 Industrial Ave, Chicago, IL", 10000);
    }

    // -----------------------------------------------------------------------
    // getAllWarehouses
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllWarehouses returns all warehouses mapped to DTOs")
    void testGetAllWarehouses_Success() {
        Warehouse warehouse2 = Warehouse.builder()
                .id(2L).name("West Coast Hub").location("456 Harbor Blvd, LA, CA")
                .capacity(5000).createdAt(LocalDateTime.now()).build();

        when(warehouseRepository.findAll()).thenReturn(Arrays.asList(warehouse, warehouse2));

        List<WarehouseResponseDto> result = warehouseService.getAllWarehouses();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Main Distribution Center");
        assertThat(result.get(1).getName()).isEqualTo("West Coast Hub");
        verify(warehouseRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------
    // createWarehouse
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createWarehouse persists a new warehouse and returns the response DTO")
    void testCreateWarehouse_Success() {
        when(warehouseRepository.existsByNameIgnoreCase("Main Distribution Center")).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseResponseDto result = warehouseService.createWarehouse(warehouseDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Main Distribution Center");
        assertThat(result.getCapacity()).isEqualTo(10000);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("createWarehouse throws DuplicateResourceException when name already exists")
    void testCreateWarehouse_DuplicateName() {
        when(warehouseRepository.existsByNameIgnoreCase("Main Distribution Center")).thenReturn(true);

        assertThatThrownBy(() -> warehouseService.createWarehouse(warehouseDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Warehouse already exists with name: Main Distribution Center");

        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    // -----------------------------------------------------------------------
    // updateWarehouse
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateWarehouse modifies an existing warehouse and returns the updated DTO")
    void testUpdateWarehouse_Success() {
        WarehouseDto updateDto = new WarehouseDto("Updated Center", "789 New Ave", 15000);

        Warehouse updatedWarehouse = Warehouse.builder()
                .id(1L).name("Updated Center").location("789 New Ave")
                .capacity(15000).createdAt(LocalDateTime.now()).build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updatedWarehouse);

        WarehouseResponseDto result = warehouseService.updateWarehouse(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Center");
        assertThat(result.getCapacity()).isEqualTo(15000);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    // -----------------------------------------------------------------------
    // deleteWarehouse
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteWarehouse removes the warehouse when it exists")
    void testDeleteWarehouse_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.deleteWarehouse(1L);

        verify(warehouseRepository, times(1)).delete(warehouse);
    }

    // -----------------------------------------------------------------------
    // getWarehouseById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getWarehouseById returns the correct warehouse")
    void testGetWarehouseById_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        WarehouseResponseDto result = warehouseService.getWarehouseById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Main Distribution Center");
    }

    @Test
    @DisplayName("getWarehouseById throws ResourceNotFoundException for non-existent ID")
    void testGetWarehouseById_NotFound() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getWarehouseById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Warehouse not found with id: 999");
    }

    // -----------------------------------------------------------------------
    // deleteWarehouse -- not found
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteWarehouse throws ResourceNotFoundException for non-existent ID")
    void testDeleteWarehouse_NotFound() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.deleteWarehouse(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Warehouse not found with id: 999");

        verify(warehouseRepository, never()).delete(any(Warehouse.class));
    }
}
