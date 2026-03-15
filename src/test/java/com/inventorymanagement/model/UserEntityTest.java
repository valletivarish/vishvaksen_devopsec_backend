package com.inventorymanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link User} entity.
 *
 * Verifies builder, getters/setters, and JPA lifecycle callbacks.
 */
class UserEntityTest {

    @Test
    @DisplayName("Builder creates User with all fields set correctly")
    void testBuilder() {
        User user = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .password("hashed")
                .fullName("Admin User")
                .role(Role.ADMIN)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getEmail()).isEqualTo("admin@test.com");
        assertThat(user.getPassword()).isEqualTo("hashed");
        assertThat(user.getFullName()).isEqualTo("Admin User");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("onCreate sets createdAt timestamp")
    void testOnCreate() {
        User user = new User();
        user.onCreate();

        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("All Role enum values exist")
    void testRoleEnum() {
        assertThat(Role.values()).containsExactly(Role.ADMIN, Role.MANAGER, Role.USER);
    }

    @Test
    @DisplayName("All MovementType enum values exist")
    void testMovementTypeEnum() {
        assertThat(MovementType.values()).containsExactly(MovementType.IN, MovementType.OUT, MovementType.TRANSFER);
    }

    @Test
    @DisplayName("Setters modify user fields correctly")
    void testSetters() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setRole(Role.USER);

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }
}
