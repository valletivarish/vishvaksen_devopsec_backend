package com.inventorymanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Supplier} entity.
 *
 * Verifies builder, getters/setters, and JPA lifecycle callbacks.
 */
class SupplierEntityTest {

    @Test
    @DisplayName("Builder creates Supplier with all fields set correctly")
    void testBuilder() {
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("Acme Corp")
                .contactEmail("acme@test.com")
                .phone("+1234567890")
                .address("123 Main St")
                .build();

        assertThat(supplier.getId()).isEqualTo(1L);
        assertThat(supplier.getName()).isEqualTo("Acme Corp");
        assertThat(supplier.getContactEmail()).isEqualTo("acme@test.com");
        assertThat(supplier.getPhone()).isEqualTo("+1234567890");
        assertThat(supplier.getAddress()).isEqualTo("123 Main St");
    }

    @Test
    @DisplayName("onCreate sets createdAt timestamp")
    void testOnCreate() {
        Supplier supplier = new Supplier();
        supplier.onCreate();

        assertThat(supplier.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Setters modify supplier fields correctly")
    void testSetters() {
        Supplier supplier = new Supplier();
        supplier.setName("Updated");
        supplier.setContactEmail("new@test.com");

        assertThat(supplier.getName()).isEqualTo("Updated");
        assertThat(supplier.getContactEmail()).isEqualTo("new@test.com");
    }
}
