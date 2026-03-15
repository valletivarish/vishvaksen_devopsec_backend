package com.inventorymanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link StockMovement} entity.
 *
 * Verifies builder, getters/setters, and JPA lifecycle callbacks.
 */
class StockMovementEntityTest {

    @Test
    @DisplayName("Builder creates StockMovement with all fields set correctly")
    void testBuilder() {
        Product product = Product.builder().id(1L).name("Laptop").build();
        Warehouse warehouse = Warehouse.builder().id(1L).name("Main").build();
        LocalDateTime movementDate = LocalDateTime.of(2025, 6, 15, 10, 30);

        StockMovement movement = StockMovement.builder()
                .id(1L)
                .product(product)
                .warehouse(warehouse)
                .quantity(50)
                .type(MovementType.IN)
                .referenceNumber("PO-001")
                .notes("Initial delivery")
                .movementDate(movementDate)
                .build();

        assertThat(movement.getId()).isEqualTo(1L);
        assertThat(movement.getProduct().getName()).isEqualTo("Laptop");
        assertThat(movement.getWarehouse().getName()).isEqualTo("Main");
        assertThat(movement.getQuantity()).isEqualTo(50);
        assertThat(movement.getType()).isEqualTo(MovementType.IN);
        assertThat(movement.getReferenceNumber()).isEqualTo("PO-001");
        assertThat(movement.getNotes()).isEqualTo("Initial delivery");
        assertThat(movement.getMovementDate()).isEqualTo(movementDate);
    }

    @Test
    @DisplayName("onCreate sets createdAt and defaults movementDate when null")
    void testOnCreate_DefaultsMovementDate() {
        StockMovement movement = new StockMovement();
        movement.onCreate();

        assertThat(movement.getCreatedAt()).isNotNull();
        assertThat(movement.getMovementDate()).isNotNull();
    }

    @Test
    @DisplayName("onCreate preserves existing movementDate")
    void testOnCreate_PreservesMovementDate() {
        StockMovement movement = new StockMovement();
        LocalDateTime specificDate = LocalDateTime.of(2025, 1, 1, 12, 0);
        movement.setMovementDate(specificDate);

        movement.onCreate();

        assertThat(movement.getMovementDate()).isEqualTo(specificDate);
        assertThat(movement.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Setters modify fields correctly")
    void testSetters() {
        StockMovement movement = new StockMovement();
        movement.setQuantity(100);
        movement.setType(MovementType.OUT);
        movement.setReferenceNumber("SO-001");
        movement.setNotes("Sales order");

        assertThat(movement.getQuantity()).isEqualTo(100);
        assertThat(movement.getType()).isEqualTo(MovementType.OUT);
        assertThat(movement.getReferenceNumber()).isEqualTo("SO-001");
        assertThat(movement.getNotes()).isEqualTo("Sales order");
    }
}
