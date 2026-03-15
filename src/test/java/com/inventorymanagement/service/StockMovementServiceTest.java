package com.inventorymanagement.service;

import com.inventorymanagement.dto.StockMovementDto;
import com.inventorymanagement.dto.StockMovementResponseDto;
import com.inventorymanagement.exception.InsufficientStockException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Category;
import com.inventorymanagement.model.MovementType;
import com.inventorymanagement.model.Product;
import com.inventorymanagement.model.StockMovement;
import com.inventorymanagement.model.Supplier;
import com.inventorymanagement.model.Warehouse;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.StockMovementRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
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
 * Unit tests for {@link StockMovementService}.
 *
 * Focuses on the critical business logic: stock-level adjustments during
 * IN/OUT movements, insufficient-stock validation for outbound movements,
 * and entity resolution (product/warehouse not found).
 */
@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private StockMovementService stockMovementService;

    private Product product;
    private Warehouse warehouse;
    private StockMovement stockMovement;

    @BeforeEach
    void setUp() {
        Category category = Category.builder().id(1L).name("Electronics").build();
        Supplier supplier = Supplier.builder().id(1L).name("Acme Corp").build();

        product = Product.builder()
                .id(1L)
                .name("Wireless Keyboard")
                .sku("KB-WIRELESS-001")
                .unitPrice(new BigDecimal("49.99"))
                .reorderLevel(10)
                .currentStock(50)
                .category(category)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Main Warehouse")
                .location("123 Industrial Ave")
                .capacity(10000)
                .createdAt(LocalDateTime.now())
                .build();

        stockMovement = StockMovement.builder()
                .id(1L)
                .product(product)
                .warehouse(warehouse)
                .quantity(20)
                .type(MovementType.IN)
                .referenceNumber("PO-001")
                .notes("Initial stock")
                .movementDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // -----------------------------------------------------------------------
    // createMovement -- Stock IN
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createMovement with IN type increases the product currentStock")
    void testCreateMovement_StockIn_Success() {
        // Arrange -- create an IN movement for 20 units
        StockMovementDto dto = new StockMovementDto(
                1L, 1L, 20, MovementType.IN, "PO-001", "Restock"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(stockMovement);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        StockMovementResponseDto result = stockMovementService.createMovement(dto);

        // Assert -- stock should increase from 50 to 70
        assertThat(result).isNotNull();
        assertThat(product.getCurrentStock()).isEqualTo(70);
        verify(productRepository, times(1)).save(product);
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    // -----------------------------------------------------------------------
    // createMovement -- Stock OUT
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createMovement with OUT type decreases the product currentStock")
    void testCreateMovement_StockOut_Success() {
        // Arrange -- create an OUT movement for 10 units (product has 50 in stock)
        StockMovementDto dto = new StockMovementDto(
                1L, 1L, 10, MovementType.OUT, "SO-001", "Order fulfillment"
        );

        StockMovement outMovement = StockMovement.builder()
                .id(2L).product(product).warehouse(warehouse).quantity(10)
                .type(MovementType.OUT).movementDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now()).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(outMovement);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        StockMovementResponseDto result = stockMovementService.createMovement(dto);

        // Assert -- stock should decrease from 50 to 40
        assertThat(result).isNotNull();
        assertThat(product.getCurrentStock()).isEqualTo(40);
        verify(productRepository, times(1)).save(product);
    }

    // -----------------------------------------------------------------------
    // createMovement -- Insufficient stock
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createMovement with OUT type throws InsufficientStockException when stock is too low")
    void testCreateMovement_StockOut_InsufficientStock() {
        // Arrange -- try to dispatch 100 units when only 50 are available
        StockMovementDto dto = new StockMovementDto(
                1L, 1L, 100, MovementType.OUT, "SO-002", "Large order"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        // Act & Assert
        assertThatThrownBy(() -> stockMovementService.createMovement(dto))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock for Wireless Keyboard");

        // Verify that neither the movement nor the product was saved
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    // -----------------------------------------------------------------------
    // createMovement -- Product not found
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createMovement throws ResourceNotFoundException when product does not exist")
    void testCreateMovement_ProductNotFound() {
        StockMovementDto dto = new StockMovementDto(
                999L, 1L, 10, MovementType.IN, "PO-002", "Test"
        );

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockMovementService.createMovement(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }

    // -----------------------------------------------------------------------
    // createMovement -- Warehouse not found
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createMovement throws ResourceNotFoundException when warehouse does not exist")
    void testCreateMovement_WarehouseNotFound() {
        StockMovementDto dto = new StockMovementDto(
                1L, 999L, 10, MovementType.IN, "PO-003", "Test"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockMovementService.createMovement(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Warehouse not found with id: 999");
    }

    // -----------------------------------------------------------------------
    // getRecentMovements
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getRecentMovements returns the most recent movements")
    void testGetRecentMovements_Success() {
        when(stockMovementRepository.findTop10ByOrderByCreatedAtDesc())
                .thenReturn(Collections.singletonList(stockMovement));

        List<StockMovementResponseDto> result = stockMovementService.getRecentMovements();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("Wireless Keyboard");
        assertThat(result.get(0).getWarehouseName()).isEqualTo("Main Warehouse");
        verify(stockMovementRepository, times(1)).findTop10ByOrderByCreatedAtDesc();
    }
}
