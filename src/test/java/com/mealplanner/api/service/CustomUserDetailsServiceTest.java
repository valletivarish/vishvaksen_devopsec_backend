package com.mealplanner.api.service;

import com.mealplanner.api.model.User;
import com.mealplanner.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CustomUserDetailsService.
 * Covers happy path and user-not-found exception.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        User user = User.builder()
                .id(1L)
                .username("john")
                .password("encodedPassword")
                .email("john@example.com")
                .fullName("John Doe")
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("john");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void loadUserByUsername_returnsCorrectPasswordHash() {
        User user = User.builder()
                .id(2L)
                .username("alice")
                .password("$2a$10$hashedPassword")
                .email("alice@example.com")
                .fullName("Alice Smith")
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("alice");

        assertThat(result.getPassword()).isEqualTo("$2a$10$hashedPassword");
    }
}
