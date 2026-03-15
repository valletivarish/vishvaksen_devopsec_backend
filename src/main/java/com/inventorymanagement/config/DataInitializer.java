package com.inventorymanagement.config;

import com.inventorymanagement.model.*;
import com.inventorymanagement.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seeds the database with demo data on application startup.
 *
 * This initializer only runs when the database is empty (i.e., no users exist),
 * ensuring that existing data is never overwritten. It creates a representative
 * set of users, categories, suppliers, warehouses, products, and stock movements
 * so that the system can be explored immediately after a fresh deployment.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           SupplierRepository supplierRepository,
                           WarehouseRepository warehouseRepository,
                           ProductRepository productRepository,
                           StockMovementRepository stockMovementRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only seed data if the database is empty to avoid duplicate entries
        if (userRepository.count() > 0) {
            logger.info("Database already contains data -- skipping demo data initialization");
            return;
        }

        // -----------------------------------------------------------------------
        // 1. Users -- one admin and one regular user for demo/testing purposes
        // -----------------------------------------------------------------------
        User admin = User.builder()
                .username("admin")
                .email("admin@inventory.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Admin User")
                .role(Role.ADMIN)
                .build();

        User user = User.builder()
                .username("user")
                .email("user@inventory.com")
                .password(passwordEncoder.encode("user123"))
                .fullName("Demo User")
                .role(Role.USER)
                .build();

        userRepository.save(admin);
        userRepository.save(user);

        // -----------------------------------------------------------------------
        // 2. Categories -- four broad groupings covering common inventory types
        // -----------------------------------------------------------------------
        Category electronics = categoryRepository.save(Category.builder()
                .name("Electronics")
                .description("Electronic devices and components")
                .build());

        Category officeSupplies = categoryRepository.save(Category.builder()
                .name("Office Supplies")
                .description("Office stationery and supplies")
                .build());

        Category furniture = categoryRepository.save(Category.builder()
                .name("Furniture")
                .description("Office and warehouse furniture")
                .build());

        Category rawMaterials = categoryRepository.save(Category.builder()
                .name("Raw Materials")
                .description("Manufacturing raw materials")
                .build());

        // -----------------------------------------------------------------------
        // 3. Suppliers -- three vendors representing different supply chains
        // -----------------------------------------------------------------------
        Supplier techCorp = supplierRepository.save(Supplier.builder()
                .name("TechCorp Solutions")
                .contactEmail("tech@techcorp.com")
                .phone("+1-555-0101")
                .address("123 Tech Street, Silicon Valley, CA")
                .build());

        Supplier officeWorld = supplierRepository.save(Supplier.builder()
                .name("Office World Ltd")
                .contactEmail("sales@officeworld.com")
                .phone("+1-555-0202")
                .address("456 Business Ave, New York, NY")
                .build());

        Supplier globalMaterials = supplierRepository.save(Supplier.builder()
                .name("Global Materials Inc")
                .contactEmail("info@globalmaterials.com")
                .phone("+1-555-0303")
                .address("789 Industry Rd, Chicago, IL")
                .build());

        // -----------------------------------------------------------------------
        // 4. Warehouses -- three storage locations with varying capacities
        // -----------------------------------------------------------------------
        Warehouse mainWarehouse = warehouseRepository.save(Warehouse.builder()
                .name("Main Warehouse")
                .location("Building A, Industrial Park, Dublin")
                .capacity(10000)
                .build());

        Warehouse secondaryStorage = warehouseRepository.save(Warehouse.builder()
                .name("Secondary Storage")
                .location("Unit 5, Commerce Centre, Cork")
                .capacity(5000)
                .build());

        Warehouse distributionCentre = warehouseRepository.save(Warehouse.builder()
                .name("Distribution Centre")
                .location("Dock 12, Port Area, Galway")
                .capacity(8000)
                .build());

        // -----------------------------------------------------------------------
        // 5. Products -- six items across all categories, including two that are
        //    intentionally below their reorder level to demonstrate low-stock alerts
        // -----------------------------------------------------------------------
        Product laptopPro = productRepository.save(Product.builder()
                .name("Laptop Pro 15")
                .sku("ELEC-001")
                .unitPrice(new BigDecimal("999.99"))
                .reorderLevel(10)
                .currentStock(50)
                .category(electronics)
                .supplier(techCorp)
                .build());

        Product wirelessMouse = productRepository.save(Product.builder()
                .name("Wireless Mouse")
                .sku("ELEC-002")
                .unitPrice(new BigDecimal("29.99"))
                .reorderLevel(50)
                .currentStock(200)
                .category(electronics)
                .supplier(techCorp)
                .build());

        Product a4Paper = productRepository.save(Product.builder()
                .name("A4 Paper Ream")
                .sku("OFF-001")
                .unitPrice(new BigDecimal("5.99"))
                .reorderLevel(100)
                .currentStock(500)
                .category(officeSupplies)
                .supplier(officeWorld)
                .build());

        // LOW STOCK: currentStock (150) is below reorderLevel (200) -- triggers alert
        Product penBox = productRepository.save(Product.builder()
                .name("Ballpoint Pen Box")
                .sku("OFF-002")
                .unitPrice(new BigDecimal("3.49"))
                .reorderLevel(200)
                .currentStock(150)
                .category(officeSupplies)
                .supplier(officeWorld)
                .build());

        // LOW STOCK: currentStock (3) is below reorderLevel (5) -- triggers alert
        Product officeDesk = productRepository.save(Product.builder()
                .name("Office Desk Standard")
                .sku("FURN-001")
                .unitPrice(new BigDecimal("249.99"))
                .reorderLevel(5)
                .currentStock(3)
                .category(furniture)
                .supplier(globalMaterials)
                .build());

        Product steelSheet = productRepository.save(Product.builder()
                .name("Steel Sheet 2mm")
                .sku("RAW-001")
                .unitPrice(new BigDecimal("45.00"))
                .reorderLevel(30)
                .currentStock(100)
                .category(rawMaterials)
                .supplier(globalMaterials)
                .build());

        // -----------------------------------------------------------------------
        // 6. Stock Movements -- sample inbound and outbound movements to populate
        //    the movement history and demonstrate audit trail functionality
        // -----------------------------------------------------------------------

        // Initial stock delivery of laptops into Main Warehouse
        stockMovementRepository.save(StockMovement.builder()
                .product(laptopPro)
                .warehouse(mainWarehouse)
                .quantity(50)
                .type(MovementType.IN)
                .referenceNumber("PO-2025-001")
                .notes("Initial stock delivery")
                .movementDate(LocalDateTime.now())
                .build());

        // Bulk purchase delivery of wireless mice into Main Warehouse
        stockMovementRepository.save(StockMovement.builder()
                .product(wirelessMouse)
                .warehouse(mainWarehouse)
                .quantity(200)
                .type(MovementType.IN)
                .referenceNumber("PO-2025-002")
                .notes("Bulk purchase delivery")
                .movementDate(LocalDateTime.now())
                .build());

        // Outbound sales order fulfillment -- 5 laptops shipped from Main Warehouse
        stockMovementRepository.save(StockMovement.builder()
                .product(laptopPro)
                .warehouse(mainWarehouse)
                .quantity(5)
                .type(MovementType.OUT)
                .referenceNumber("SO-2025-001")
                .notes("Sales order fulfillment")
                .movementDate(LocalDateTime.now())
                .build());

        // Monthly supply restock of A4 paper into Secondary Storage
        stockMovementRepository.save(StockMovement.builder()
                .product(a4Paper)
                .warehouse(secondaryStorage)
                .quantity(500)
                .type(MovementType.IN)
                .referenceNumber("PO-2025-003")
                .notes("Monthly supply restock")
                .movementDate(LocalDateTime.now())
                .build());

        logger.info("Demo data initialized successfully");
    }
}
