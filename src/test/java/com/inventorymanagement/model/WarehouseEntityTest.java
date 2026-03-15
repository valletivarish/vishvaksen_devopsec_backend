package com.inventorymanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Warehouse} entity.
 *
 * Verifies builder, getters/setters, and JPA lifecycle callbacks.
 */
class WarehouseEntityTest {

    @Test
    @DisplayName("Builder creates Warehouse with all fields set correctly")
    void testBuilder() {
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .name("Main Warehouse")
                .location("Dublin")
                .capacity(10000)
                .build();

        assertThat(warehouse.getId()).isEqualTo(1L);
        assertThat(warehouse.getName()).isEqualTo("Main Warehouse");
        assertThat(warehouse.getLocation()).isEqualTo("Dublin");
        assertThat(warehouse.getCapacity()).isEqualTo(10000);
    }

    @Test
    @DisplayName("onCreate sets createdAt timestamp")
    void testOnCreate() {
        Warehouse warehouse = new Warehouse();
        warehouse.onCreate();

        assertThat(warehouse.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Setters modify warehouse fields correctly")
    void testSetters() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Updated");
        warehouse.setLocation("Cork");
        warehouse.setCapacity(5000);

        assertThat(warehouse.getName()).isEqualTo("Updated");
        assertThat(warehouse.getLocation()).isEqualTo("Cork");
        assertThat(warehouse.getCapacity()).isEqualTo(5000);
    }
}
