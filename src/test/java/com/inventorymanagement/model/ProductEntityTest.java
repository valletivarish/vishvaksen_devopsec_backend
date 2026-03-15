package com.inventorymanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Product} entity.
 *
 * Verifies builder, getters/setters, and JPA lifecycle callbacks.
 */
class ProductEntityTest {

    @Test
    @DisplayName("Builder creates Product with all fields set correctly")
    void testBuilder() {
        Category category = Category.builder().id(1L).name("Electronics").build();
        Supplier supplier = Supplier.builder().id(1L).name("Acme").build();

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .sku("ELEC-001")
                .description("A laptop")
                .unitPrice(new BigDecimal("999.99"))
                .reorderLevel(10)
                .currentStock(50)
                .category(category)
                .supplier(supplier)
                .build();

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Laptop");
        assertThat(product.getSku()).isEqualTo("ELEC-001");
        assertThat(product.getDescription()).isEqualTo("A laptop");
        assertThat(product.getUnitPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(product.getReorderLevel()).isEqualTo(10);
        assertThat(product.getCurrentStock()).isEqualTo(50);
        assertThat(product.getCategory().getName()).isEqualTo("Electronics");
        assertThat(product.getSupplier().getName()).isEqualTo("Acme");
    }

    @Test
    @DisplayName("onCreate sets createdAt and updatedAt timestamps")
    void testOnCreate() {
        Product product = new Product();
        product.onCreate();

        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
        assertThat(product.getCreatedAt()).isEqualTo(product.getUpdatedAt());
    }

    @Test
    @DisplayName("onUpdate refreshes updatedAt timestamp")
    void testOnUpdate() {
        Product product = new Product();
        product.onCreate();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();

        product.onUpdate();

        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    @DisplayName("Setters modify fields correctly")
    void testSetters() {
        Product product = new Product();
        product.setName("Updated Name");
        product.setSku("NEW-SKU");
        product.setCurrentStock(100);
        product.setUnitPrice(new BigDecimal("25.50"));

        assertThat(product.getName()).isEqualTo("Updated Name");
        assertThat(product.getSku()).isEqualTo("NEW-SKU");
        assertThat(product.getCurrentStock()).isEqualTo(100);
        assertThat(product.getUnitPrice()).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    @DisplayName("NoArgsConstructor creates an empty Product")
    void testNoArgsConstructor() {
        Product product = new Product();
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isNull();
    }
}
