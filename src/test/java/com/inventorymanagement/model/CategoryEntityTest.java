package com.inventorymanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Category} entity.
 *
 * Verifies builder, getters/setters, and JPA lifecycle callbacks.
 */
class CategoryEntityTest {

    @Test
    @DisplayName("Builder creates Category with all fields set correctly")
    void testBuilder() {
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices")
                .build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Electronics");
        assertThat(category.getDescription()).isEqualTo("Electronic devices");
    }

    @Test
    @DisplayName("onCreate sets createdAt timestamp")
    void testOnCreate() {
        Category category = new Category();
        category.onCreate();

        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Setters modify category fields correctly")
    void testSetters() {
        Category category = new Category();
        category.setName("Updated");
        category.setDescription("Updated desc");

        assertThat(category.getName()).isEqualTo("Updated");
        assertThat(category.getDescription()).isEqualTo("Updated desc");
    }
}
