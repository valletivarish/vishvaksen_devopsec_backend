package com.inventorymanagement.dto;

import com.inventorymanagement.model.MovementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for all DTO classes ensuring Lombok-generated methods
 * (getters, setters, equals, hashCode, toString, constructors, builders)
 * work correctly and achieve full code coverage.
 */
class DtoTest {

    // -----------------------------------------------------------------------
    // ProductDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("ProductDto getters and setters work correctly")
    void testProductDto_GettersSetters() {
        ProductDto dto = new ProductDto();
        dto.setName("Laptop");
        dto.setSku("ELEC-001");
        dto.setDescription("A laptop");
        dto.setUnitPrice(new BigDecimal("999.99"));
        dto.setReorderLevel(10);
        dto.setCategoryId(1L);
        dto.setSupplierId(2L);

        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getSku()).isEqualTo("ELEC-001");
        assertThat(dto.getDescription()).isEqualTo("A laptop");
        assertThat(dto.getUnitPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(dto.getReorderLevel()).isEqualTo(10);
        assertThat(dto.getCategoryId()).isEqualTo(1L);
        assertThat(dto.getSupplierId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("ProductDto AllArgsConstructor and equals/hashCode")
    void testProductDto_AllArgs() {
        ProductDto dto1 = new ProductDto("Laptop", "ELEC-001", "desc",
                new BigDecimal("999.99"), 10, 1L, 2L);
        ProductDto dto2 = new ProductDto("Laptop", "ELEC-001", "desc",
                new BigDecimal("999.99"), 10, 1L, 2L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("Laptop");
    }

    // -----------------------------------------------------------------------
    // ProductResponseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("ProductResponseDto builder and getters work correctly")
    void testProductResponseDto_Builder() {
        LocalDateTime now = LocalDateTime.now();
        ProductResponseDto dto = ProductResponseDto.builder()
                .id(1L).name("Laptop").sku("ELEC-001").description("desc")
                .unitPrice(new BigDecimal("999.99")).reorderLevel(10)
                .categoryId(1L).categoryName("Electronics")
                .supplierId(2L).supplierName("Acme")
                .currentStock(50).createdAt(now).updatedAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getSku()).isEqualTo("ELEC-001");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getUnitPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(dto.getReorderLevel()).isEqualTo(10);
        assertThat(dto.getCategoryId()).isEqualTo(1L);
        assertThat(dto.getCategoryName()).isEqualTo("Electronics");
        assertThat(dto.getSupplierId()).isEqualTo(2L);
        assertThat(dto.getSupplierName()).isEqualTo("Acme");
        assertThat(dto.getCurrentStock()).isEqualTo(50);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("ProductResponseDto setters and equals/hashCode/toString")
    void testProductResponseDto_SettersAndEquality() {
        ProductResponseDto dto1 = new ProductResponseDto();
        dto1.setId(1L);
        dto1.setName("Laptop");
        dto1.setSku("ELEC-001");
        dto1.setDescription("desc");
        dto1.setUnitPrice(new BigDecimal("999.99"));
        dto1.setReorderLevel(10);
        dto1.setCategoryId(1L);
        dto1.setCategoryName("Electronics");
        dto1.setSupplierId(2L);
        dto1.setSupplierName("Acme");
        dto1.setCurrentStock(50);

        ProductResponseDto dto2 = new ProductResponseDto();
        dto2.setId(1L);
        dto2.setName("Laptop");
        dto2.setSku("ELEC-001");
        dto2.setDescription("desc");
        dto2.setUnitPrice(new BigDecimal("999.99"));
        dto2.setReorderLevel(10);
        dto2.setCategoryId(1L);
        dto2.setCategoryName("Electronics");
        dto2.setSupplierId(2L);
        dto2.setSupplierName("Acme");
        dto2.setCurrentStock(50);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("Laptop");
    }

    // -----------------------------------------------------------------------
    // CategoryDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CategoryDto getters, setters, equals, hashCode, toString")
    void testCategoryDto() {
        CategoryDto dto = new CategoryDto("Electronics", "Electronic devices");
        assertThat(dto.getName()).isEqualTo("Electronics");
        assertThat(dto.getDescription()).isEqualTo("Electronic devices");

        CategoryDto dto2 = new CategoryDto();
        dto2.setName("Electronics");
        dto2.setDescription("Electronic devices");

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("Electronics");
    }

    // -----------------------------------------------------------------------
    // CategoryResponseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CategoryResponseDto builder and all accessors")
    void testCategoryResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        CategoryResponseDto dto = CategoryResponseDto.builder()
                .id(1L).name("Electronics").description("desc")
                .productCount(5L).createdAt(now).build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Electronics");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getProductCount()).isEqualTo(5L);
        assertThat(dto.getCreatedAt()).isEqualTo(now);

        CategoryResponseDto dto2 = new CategoryResponseDto(1L, "Electronics", "desc", 5L, now);
        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.toString()).contains("Electronics");

        CategoryResponseDto dto3 = new CategoryResponseDto();
        dto3.setId(1L);
        dto3.setName("Electronics");
        dto3.setDescription("desc");
        dto3.setProductCount(5L);
        dto3.setCreatedAt(now);
        assertThat(dto).isEqualTo(dto3);
    }

    // -----------------------------------------------------------------------
    // SupplierDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("SupplierDto getters, setters, equals, hashCode, toString")
    void testSupplierDto() {
        SupplierDto dto = new SupplierDto("Acme", "acme@test.com", "+123", "123 Main St");
        assertThat(dto.getName()).isEqualTo("Acme");
        assertThat(dto.getContactEmail()).isEqualTo("acme@test.com");
        assertThat(dto.getPhone()).isEqualTo("+123");
        assertThat(dto.getAddress()).isEqualTo("123 Main St");

        SupplierDto dto2 = new SupplierDto();
        dto2.setName("Acme");
        dto2.setContactEmail("acme@test.com");
        dto2.setPhone("+123");
        dto2.setAddress("123 Main St");

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("Acme");
    }

    // -----------------------------------------------------------------------
    // SupplierResponseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("SupplierResponseDto builder and all accessors")
    void testSupplierResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L).name("Acme").contactEmail("acme@test.com")
                .phone("+123").address("123 St").productCount(3L)
                .createdAt(now).build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Acme");
        assertThat(dto.getContactEmail()).isEqualTo("acme@test.com");
        assertThat(dto.getPhone()).isEqualTo("+123");
        assertThat(dto.getAddress()).isEqualTo("123 St");
        assertThat(dto.getProductCount()).isEqualTo(3L);
        assertThat(dto.getCreatedAt()).isEqualTo(now);

        SupplierResponseDto dto2 = new SupplierResponseDto(1L, "Acme", "acme@test.com",
                "+123", "123 St", 3L, now);
        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.toString()).contains("Acme");

        SupplierResponseDto dto3 = new SupplierResponseDto();
        dto3.setId(1L);
        dto3.setName("Acme");
        dto3.setContactEmail("acme@test.com");
        dto3.setPhone("+123");
        dto3.setAddress("123 St");
        dto3.setProductCount(3L);
        dto3.setCreatedAt(now);
        assertThat(dto).isEqualTo(dto3);
    }

    // -----------------------------------------------------------------------
    // WarehouseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("WarehouseDto getters, setters, equals, hashCode, toString")
    void testWarehouseDto() {
        WarehouseDto dto = new WarehouseDto("Main", "Dublin", 10000);
        assertThat(dto.getName()).isEqualTo("Main");
        assertThat(dto.getLocation()).isEqualTo("Dublin");
        assertThat(dto.getCapacity()).isEqualTo(10000);

        WarehouseDto dto2 = new WarehouseDto();
        dto2.setName("Main");
        dto2.setLocation("Dublin");
        dto2.setCapacity(10000);

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("Main");
    }

    // -----------------------------------------------------------------------
    // WarehouseResponseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("WarehouseResponseDto builder and all accessors")
    void testWarehouseResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        WarehouseResponseDto dto = WarehouseResponseDto.builder()
                .id(1L).name("Main").location("Dublin").capacity(10000)
                .currentUtilization(5000).createdAt(now).build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Main");
        assertThat(dto.getLocation()).isEqualTo("Dublin");
        assertThat(dto.getCapacity()).isEqualTo(10000);
        assertThat(dto.getCurrentUtilization()).isEqualTo(5000);
        assertThat(dto.getCreatedAt()).isEqualTo(now);

        WarehouseResponseDto dto2 = new WarehouseResponseDto(1L, "Main", "Dublin", 10000, 5000, now);
        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.toString()).contains("Main");

        WarehouseResponseDto dto3 = new WarehouseResponseDto();
        dto3.setId(1L);
        dto3.setName("Main");
        dto3.setLocation("Dublin");
        dto3.setCapacity(10000);
        dto3.setCurrentUtilization(5000);
        dto3.setCreatedAt(now);
        assertThat(dto).isEqualTo(dto3);
    }

    // -----------------------------------------------------------------------
    // StockMovementDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("StockMovementDto getters, setters, equals, hashCode, toString")
    void testStockMovementDto() {
        StockMovementDto dto = new StockMovementDto(1L, 2L, 50, MovementType.IN, "PO-001", "notes");
        assertThat(dto.getProductId()).isEqualTo(1L);
        assertThat(dto.getWarehouseId()).isEqualTo(2L);
        assertThat(dto.getQuantity()).isEqualTo(50);
        assertThat(dto.getType()).isEqualTo(MovementType.IN);
        assertThat(dto.getReferenceNumber()).isEqualTo("PO-001");
        assertThat(dto.getNotes()).isEqualTo("notes");

        StockMovementDto dto2 = new StockMovementDto();
        dto2.setProductId(1L);
        dto2.setWarehouseId(2L);
        dto2.setQuantity(50);
        dto2.setType(MovementType.IN);
        dto2.setReferenceNumber("PO-001");
        dto2.setNotes("notes");

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("PO-001");
    }

    // -----------------------------------------------------------------------
    // StockMovementResponseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("StockMovementResponseDto builder and all accessors")
    void testStockMovementResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        StockMovementResponseDto dto = StockMovementResponseDto.builder()
                .id(1L).productId(2L).productName("Laptop")
                .warehouseId(3L).warehouseName("Main")
                .quantity(50).type(MovementType.IN)
                .referenceNumber("PO-001").notes("delivery")
                .movementDate(now).build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getProductId()).isEqualTo(2L);
        assertThat(dto.getProductName()).isEqualTo("Laptop");
        assertThat(dto.getWarehouseId()).isEqualTo(3L);
        assertThat(dto.getWarehouseName()).isEqualTo("Main");
        assertThat(dto.getQuantity()).isEqualTo(50);
        assertThat(dto.getType()).isEqualTo(MovementType.IN);
        assertThat(dto.getReferenceNumber()).isEqualTo("PO-001");
        assertThat(dto.getNotes()).isEqualTo("delivery");
        assertThat(dto.getMovementDate()).isEqualTo(now);

        StockMovementResponseDto dto2 = new StockMovementResponseDto(1L, 2L, "Laptop",
                3L, "Main", 50, MovementType.IN, "PO-001", "delivery", now);
        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.toString()).contains("Laptop");

        StockMovementResponseDto dto3 = new StockMovementResponseDto();
        dto3.setId(1L);
        dto3.setProductId(2L);
        dto3.setProductName("Laptop");
        dto3.setWarehouseId(3L);
        dto3.setWarehouseName("Main");
        dto3.setQuantity(50);
        dto3.setType(MovementType.IN);
        dto3.setReferenceNumber("PO-001");
        dto3.setNotes("delivery");
        dto3.setMovementDate(now);
        assertThat(dto).isEqualTo(dto3);
    }

    // -----------------------------------------------------------------------
    // AuthRequestDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("AuthRequestDto getters, setters, equals, hashCode, toString")
    void testAuthRequestDto() {
        AuthRequestDto dto = new AuthRequestDto("admin", "admin123");
        assertThat(dto.getUsername()).isEqualTo("admin");
        assertThat(dto.getPassword()).isEqualTo("admin123");

        AuthRequestDto dto2 = new AuthRequestDto();
        dto2.setUsername("admin");
        dto2.setPassword("admin123");

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("admin");
    }

    // -----------------------------------------------------------------------
    // AuthResponseDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("AuthResponseDto builder and all accessors")
    void testAuthResponseDto() {
        AuthResponseDto dto = AuthResponseDto.builder()
                .token("jwt-token").username("admin").role("ADMIN").build();

        assertThat(dto.getToken()).isEqualTo("jwt-token");
        assertThat(dto.getUsername()).isEqualTo("admin");
        assertThat(dto.getRole()).isEqualTo("ADMIN");

        AuthResponseDto dto2 = new AuthResponseDto("jwt-token", "admin", "ADMIN");
        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.toString()).contains("admin");

        AuthResponseDto dto3 = new AuthResponseDto();
        dto3.setToken("jwt-token");
        dto3.setUsername("admin");
        dto3.setRole("ADMIN");
        assertThat(dto).isEqualTo(dto3);
    }

    // -----------------------------------------------------------------------
    // RegisterRequestDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("RegisterRequestDto getters, setters, equals, hashCode, toString")
    void testRegisterRequestDto() {
        RegisterRequestDto dto = new RegisterRequestDto("user", "user@test.com", "pass123", "Test User");
        assertThat(dto.getUsername()).isEqualTo("user");
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getPassword()).isEqualTo("pass123");
        assertThat(dto.getFullName()).isEqualTo("Test User");

        RegisterRequestDto dto2 = new RegisterRequestDto();
        dto2.setUsername("user");
        dto2.setEmail("user@test.com");
        dto2.setPassword("pass123");
        dto2.setFullName("Test User");

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("user");
    }

    // -----------------------------------------------------------------------
    // DashboardDto
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DashboardDto builder and all accessors")
    void testDashboardDto() {
        DashboardDto dto = DashboardDto.builder()
                .totalProducts(10).totalCategories(4)
                .totalWarehouses(3).totalSuppliers(5)
                .lowStockProducts(Collections.emptyList())
                .recentMovements(Collections.emptyList())
                .totalStockValue(new BigDecimal("50000.00"))
                .build();

        assertThat(dto.getTotalProducts()).isEqualTo(10);
        assertThat(dto.getTotalCategories()).isEqualTo(4);
        assertThat(dto.getTotalWarehouses()).isEqualTo(3);
        assertThat(dto.getTotalSuppliers()).isEqualTo(5);
        assertThat(dto.getLowStockProducts()).isEmpty();
        assertThat(dto.getRecentMovements()).isEmpty();
        assertThat(dto.getTotalStockValue()).isEqualByComparingTo(new BigDecimal("50000.00"));

        DashboardDto dto2 = new DashboardDto();
        dto2.setTotalProducts(10);
        dto2.setTotalCategories(4);
        dto2.setTotalWarehouses(3);
        dto2.setTotalSuppliers(5);
        dto2.setLowStockProducts(Collections.emptyList());
        dto2.setRecentMovements(Collections.emptyList());
        dto2.setTotalStockValue(new BigDecimal("50000.00"));

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.toString()).contains("50000");
    }

    @Test
    @DisplayName("DashboardDto AllArgsConstructor")
    void testDashboardDto_AllArgs() {
        DashboardDto dto = new DashboardDto(10, 4, 3, 5,
                Collections.emptyList(), Collections.emptyList(),
                new BigDecimal("50000.00"));

        assertThat(dto.getTotalProducts()).isEqualTo(10);
        assertThat(dto.getTotalCategories()).isEqualTo(4);
    }
}
