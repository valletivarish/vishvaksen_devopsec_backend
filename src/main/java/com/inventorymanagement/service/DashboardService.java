package com.inventorymanagement.service;

import com.inventorymanagement.dto.DashboardDto;
import com.inventorymanagement.dto.ProductResponseDto;
import com.inventorymanagement.dto.StockMovementResponseDto;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.SupplierRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer for the dashboard overview.
 *
 * Aggregates key metrics from across the inventory system into a single
 * response, reducing the number of API calls the frontend needs on initial
 * load. This includes entity counts, low-stock alerts, recent activity,
 * and total inventory valuation.
 */
@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductService productService;
    private final StockMovementService stockMovementService;

    /**
     * Constructor injection of all dependencies required for dashboard aggregation.
     *
     * @param productRepository    repository for product count and stock value queries
     * @param categoryRepository   repository for category count
     * @param warehouseRepository  repository for warehouse count
     * @param supplierRepository   repository for supplier count
     * @param productService       service for retrieving low-stock products
     * @param stockMovementService service for retrieving recent stock movements
     */
    public DashboardService(ProductRepository productRepository,
                            CategoryRepository categoryRepository,
                            WarehouseRepository warehouseRepository,
                            SupplierRepository supplierRepository,
                            ProductService productService,
                            StockMovementService stockMovementService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.supplierRepository = supplierRepository;
        this.productService = productService;
        this.stockMovementService = stockMovementService;
    }

    /**
     * Builds the dashboard summary containing all key inventory metrics.
     *
     * Aggregated data includes:
     * - Total counts for products, categories, warehouses, and suppliers.
     * - Products whose stock level is at or below their reorder threshold,
     *   surfacing restocking alerts.
     * - The 10 most recent stock movements for a quick activity overview.
     * - Total stock value computed as the sum of (unitPrice * currentStock)
     *   across all products, giving a high-level financial snapshot.
     *
     * @return a DashboardDto containing all aggregated metrics
     */
    @Transactional(readOnly = true)
    public DashboardDto getDashboardSummary() {
        // Retrieve entity counts from each repository
        long totalProducts = productRepository.countByDeletedFalse();
        long totalCategories = categoryRepository.countByDeletedFalse();
        long totalWarehouses = warehouseRepository.countByDeletedFalse();
        long totalSuppliers = supplierRepository.countByDeletedFalse();

        List<ProductResponseDto> lowStockProducts = productService.getLowStockProducts();

        List<StockMovementResponseDto> recentMovements = stockMovementService.getRecentMovements();

        BigDecimal totalStockValue = productRepository.findByDeletedFalse().stream()
                .map(product -> product.getUnitPrice()
                        .multiply(BigDecimal.valueOf(product.getCurrentStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardDto.builder()
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .totalWarehouses(totalWarehouses)
                .totalSuppliers(totalSuppliers)
                .lowStockProducts(lowStockProducts)
                .recentMovements(recentMovements)
                .totalStockValue(totalStockValue)
                .build();
    }
}
