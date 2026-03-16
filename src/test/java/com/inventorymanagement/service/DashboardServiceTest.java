package com.inventorymanagement.service;

import com.inventorymanagement.dto.DashboardDto;
import com.inventorymanagement.model.Category;
import com.inventorymanagement.model.Product;
import com.inventorymanagement.model.Supplier;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.SupplierRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductService productService;

    @Mock
    private StockMovementService stockMovementService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("getDashboardSummary returns aggregated metrics")
    void testGetDashboardSummary_Success() {
        when(productRepository.countByDeletedFalse()).thenReturn(10L);
        when(categoryRepository.countByDeletedFalse()).thenReturn(3L);
        when(warehouseRepository.countByDeletedFalse()).thenReturn(2L);
        when(supplierRepository.countByDeletedFalse()).thenReturn(5L);
        when(productService.getLowStockProducts()).thenReturn(Collections.emptyList());
        when(stockMovementService.getRecentMovements()).thenReturn(Collections.emptyList());

        Category category = Category.builder().id(1L).name("Electronics").build();
        Supplier supplier = Supplier.builder().id(1L).name("Acme").build();

        Product p1 = Product.builder()
                .id(1L).name("Product A").unitPrice(new BigDecimal("10.00"))
                .currentStock(100).category(category).supplier(supplier)
                .createdAt(LocalDateTime.now()).build();
        Product p2 = Product.builder()
                .id(2L).name("Product B").unitPrice(new BigDecimal("25.50"))
                .currentStock(20).category(category).supplier(supplier)
                .createdAt(LocalDateTime.now()).build();

        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(p1, p2));

        DashboardDto result = dashboardService.getDashboardSummary();

        assertThat(result.getTotalProducts()).isEqualTo(10L);
        assertThat(result.getTotalCategories()).isEqualTo(3L);
        assertThat(result.getTotalWarehouses()).isEqualTo(2L);
        assertThat(result.getTotalSuppliers()).isEqualTo(5L);
        assertThat(result.getLowStockProducts()).isEmpty();
        assertThat(result.getRecentMovements()).isEmpty();
        // 10*100 + 25.50*20 = 1000 + 510 = 1510
        assertThat(result.getTotalStockValue()).isEqualByComparingTo(new BigDecimal("1510.00"));
    }

    @Test
    @DisplayName("getDashboardSummary with empty products returns zero stock value")
    void testGetDashboardSummary_EmptyProducts() {
        when(productRepository.countByDeletedFalse()).thenReturn(0L);
        when(categoryRepository.countByDeletedFalse()).thenReturn(0L);
        when(warehouseRepository.countByDeletedFalse()).thenReturn(0L);
        when(supplierRepository.countByDeletedFalse()).thenReturn(0L);
        when(productService.getLowStockProducts()).thenReturn(Collections.emptyList());
        when(stockMovementService.getRecentMovements()).thenReturn(Collections.emptyList());
        when(productRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());

        DashboardDto result = dashboardService.getDashboardSummary();

        assertThat(result.getTotalProducts()).isZero();
        assertThat(result.getTotalStockValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
