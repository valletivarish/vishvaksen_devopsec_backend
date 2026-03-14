package com.mealplanner.api.service;

import com.mealplanner.api.dto.UserDietaryProfileRequest;
import com.mealplanner.api.dto.UserDietaryProfileResponse;
import com.mealplanner.api.exception.DuplicateResourceException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.User;
import com.mealplanner.api.model.UserDietaryProfile;
import com.mealplanner.api.repository.UserDietaryProfileRepository;
import com.mealplanner.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserDietaryProfileService.
 * Covers CRUD operations, duplicate profile detection, and not-found scenarios.
 */
@ExtendWith(MockitoExtension.class)
class UserDietaryProfileServiceTest {

    @Mock
    private UserDietaryProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDietaryProfileService userDietaryProfileService;

    private User user;
    private UserDietaryProfile profile;
    private UserDietaryProfileRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        profile = UserDietaryProfile.builder()
                .id(10L)
                .user(user)
                .calorieGoal(2000)
                .proteinGoal(150.0)
                .carbGoal(250.0)
                .fatGoal(65.0)
                .allergies(Arrays.asList("peanuts"))
                .dietaryRestrictions(Arrays.asList("VEGETARIAN"))
                .build();
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        request = new UserDietaryProfileRequest(
                2000, 150.0, 250.0, 65.0,
                Arrays.asList("peanuts"),
                Arrays.asList("VEGETARIAN")
        );
    }

    @Test
    void getProfileByUserId_whenProfileExists_returnsResponse() {
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        UserDietaryProfileResponse result = userDietaryProfileService.getProfileByUserId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCalorieGoal()).isEqualTo(2000);
        assertThat(result.getProteinGoal()).isEqualTo(150.0);
        assertThat(result.getCarbGoal()).isEqualTo(250.0);
        assertThat(result.getFatGoal()).isEqualTo(65.0);
        assertThat(result.getAllergies()).containsExactly("peanuts");
        assertThat(result.getDietaryRestrictions()).containsExactly("VEGETARIAN");
    }

    @Test
    void getProfileByUserId_whenProfileNotFound_throwsResourceNotFoundException() {
        when(profileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDietaryProfileService.getProfileByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getProfileById_whenProfileExists_returnsResponse() {
        when(profileRepository.findById(10L)).thenReturn(Optional.of(profile));

        UserDietaryProfileResponse result = userDietaryProfileService.getProfileById(10L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCalorieGoal()).isEqualTo(2000);
    }

    @Test
    void getProfileById_whenProfileNotFound_throwsResourceNotFoundException() {
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDietaryProfileService.getProfileById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createProfile_whenUserExistsAndNoExistingProfile_returnsResponse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(profileRepository.existsByUserId(1L)).thenReturn(false);
        when(profileRepository.save(any(UserDietaryProfile.class))).thenReturn(profile);

        UserDietaryProfileResponse result = userDietaryProfileService.createProfile(request, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(profileRepository).save(any(UserDietaryProfile.class));
    }

    @Test
    void createProfile_whenUserNotFound_throwsResourceNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDietaryProfileService.createProfile(request, "unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown");

        verify(profileRepository, never()).save(any());
    }

    @Test
    void createProfile_whenProfileAlreadyExists_throwsDuplicateResourceException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(profileRepository.existsByUserId(1L)).thenReturn(true);

        assertThatThrownBy(() -> userDietaryProfileService.createProfile(request, "testuser"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("dietary profile");

        verify(profileRepository, never()).save(any());
    }

    @Test
    void createProfile_withNullAllergies_usesEmptyList() {
        UserDietaryProfileRequest reqWithNullAllergies = new UserDietaryProfileRequest(
                2000, 150.0, 250.0, 65.0, null, null
        );
        UserDietaryProfile savedProfile = UserDietaryProfile.builder()
                .id(11L)
                .user(user)
                .calorieGoal(2000)
                .proteinGoal(150.0)
                .carbGoal(250.0)
                .fatGoal(65.0)
                .allergies(List.of())
                .dietaryRestrictions(List.of())
                .build();
        savedProfile.setCreatedAt(LocalDateTime.now());
        savedProfile.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(profileRepository.existsByUserId(1L)).thenReturn(false);
        when(profileRepository.save(any(UserDietaryProfile.class))).thenReturn(savedProfile);

        UserDietaryProfileResponse result = userDietaryProfileService.createProfile(reqWithNullAllergies, "testuser");

        assertThat(result.getAllergies()).isEmpty();
        assertThat(result.getDietaryRestrictions()).isEmpty();
    }

    @Test
    void updateProfile_whenProfileExists_returnsUpdatedResponse() {
        UserDietaryProfileRequest updateRequest = new UserDietaryProfileRequest(
                2500, 180.0, 300.0, 80.0,
                Arrays.asList("shellfish"),
                Arrays.asList("GLUTEN_FREE")
        );

        UserDietaryProfile updatedProfile = UserDietaryProfile.builder()
                .id(10L)
                .user(user)
                .calorieGoal(2500)
                .proteinGoal(180.0)
                .carbGoal(300.0)
                .fatGoal(80.0)
                .allergies(Arrays.asList("shellfish"))
                .dietaryRestrictions(Arrays.asList("GLUTEN_FREE"))
                .build();
        updatedProfile.setCreatedAt(LocalDateTime.now());
        updatedProfile.setUpdatedAt(LocalDateTime.now());

        when(profileRepository.findById(10L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(UserDietaryProfile.class))).thenReturn(updatedProfile);

        UserDietaryProfileResponse result = userDietaryProfileService.updateProfile(10L, updateRequest);

        assertThat(result.getCalorieGoal()).isEqualTo(2500);
        assertThat(result.getProteinGoal()).isEqualTo(180.0);
        assertThat(result.getCarbGoal()).isEqualTo(300.0);
        assertThat(result.getFatGoal()).isEqualTo(80.0);
        assertThat(result.getAllergies()).containsExactly("shellfish");
    }

    @Test
    void updateProfile_whenProfileNotFound_throwsResourceNotFoundException() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDietaryProfileService.updateProfile(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(profileRepository, never()).save(any());
    }

    @Test
    void updateProfile_withNullAllergies_usesEmptyList() {
        UserDietaryProfileRequest reqWithNullAllergies = new UserDietaryProfileRequest(
                2000, 150.0, 250.0, 65.0, null, null
        );
        UserDietaryProfile savedProfile = UserDietaryProfile.builder()
                .id(10L).user(user)
                .calorieGoal(2000).proteinGoal(150.0).carbGoal(250.0).fatGoal(65.0)
                .allergies(List.of()).dietaryRestrictions(List.of()).build();
        savedProfile.setCreatedAt(LocalDateTime.now());
        savedProfile.setUpdatedAt(LocalDateTime.now());

        when(profileRepository.findById(10L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(UserDietaryProfile.class))).thenReturn(savedProfile);

        UserDietaryProfileResponse result = userDietaryProfileService.updateProfile(10L, reqWithNullAllergies);

        assertThat(result.getAllergies()).isEmpty();
    }

    @Test
    void deleteProfile_whenProfileExists_deletesSuccessfully() {
        when(profileRepository.existsById(10L)).thenReturn(true);

        userDietaryProfileService.deleteProfile(10L);

        verify(profileRepository).deleteById(10L);
    }

    @Test
    void deleteProfile_whenProfileNotFound_throwsResourceNotFoundException() {
        when(profileRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userDietaryProfileService.deleteProfile(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(profileRepository, never()).deleteById(any());
    }
}
