package com.mealplanner.api.service;

import com.mealplanner.api.dto.UserDietaryProfileRequest;
import com.mealplanner.api.dto.UserDietaryProfileResponse;
import com.mealplanner.api.exception.DuplicateResourceException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.User;
import com.mealplanner.api.model.UserDietaryProfile;
import com.mealplanner.api.repository.UserDietaryProfileRepository;
import com.mealplanner.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Service layer for UserDietaryProfile CRUD operations.
 * Manages dietary preferences, restrictions, allergies,
 * and nutritional goals for each user.
 */
@Service
@RequiredArgsConstructor
public class UserDietaryProfileService {

    private final UserDietaryProfileRepository profileRepository;
    private final UserRepository userRepository;

    /** Retrieves the dietary profile for a specific user */
    @Transactional(readOnly = true)
    public UserDietaryProfileResponse getProfileByUserId(Long userId) {
        UserDietaryProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary profile not found for user: " + userId));
        return toResponse(profile);
    }

    /** Retrieves a dietary profile by its own ID */
    @Transactional(readOnly = true)
    public UserDietaryProfileResponse getProfileById(Long id) {
        UserDietaryProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserDietaryProfile", id));
        return toResponse(profile);
    }

    /**
     * Creates a dietary profile for a user.
     * Each user can only have one dietary profile (one-to-one relationship).
     */
    @Transactional
    public UserDietaryProfileResponse createProfile(UserDietaryProfileRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (profileRepository.existsByUserId(user.getId())) {
            throw new DuplicateResourceException("User already has a dietary profile");
        }

        UserDietaryProfile profile = UserDietaryProfile.builder()
                .user(user)
                .calorieGoal(request.getCalorieGoal())
                .proteinGoal(request.getProteinGoal())
                .carbGoal(request.getCarbGoal())
                .fatGoal(request.getFatGoal())
                .allergies(request.getAllergies() != null ? request.getAllergies() : new ArrayList<>())
                .dietaryRestrictions(request.getDietaryRestrictions() != null ? request.getDietaryRestrictions() : new ArrayList<>())
                .build();

        return toResponse(profileRepository.save(profile));
    }

    /** Updates an existing dietary profile with new goals and restrictions */
    @Transactional
    public UserDietaryProfileResponse updateProfile(Long id, UserDietaryProfileRequest request) {
        UserDietaryProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserDietaryProfile", id));

        profile.setCalorieGoal(request.getCalorieGoal());
        profile.setProteinGoal(request.getProteinGoal());
        profile.setCarbGoal(request.getCarbGoal());
        profile.setFatGoal(request.getFatGoal());
        profile.setAllergies(request.getAllergies() != null ? request.getAllergies() : new ArrayList<>());
        profile.setDietaryRestrictions(request.getDietaryRestrictions() != null ? request.getDietaryRestrictions() : new ArrayList<>());

        return toResponse(profileRepository.save(profile));
    }

    /** Deletes a dietary profile */
    @Transactional
    public void deleteProfile(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserDietaryProfile", id);
        }
        profileRepository.deleteById(id);
    }

    /** Converts entity to response DTO */
    private UserDietaryProfileResponse toResponse(UserDietaryProfile profile) {
        return UserDietaryProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .calorieGoal(profile.getCalorieGoal())
                .proteinGoal(profile.getProteinGoal())
                .carbGoal(profile.getCarbGoal())
                .fatGoal(profile.getFatGoal())
                .allergies(profile.getAllergies())
                .dietaryRestrictions(profile.getDietaryRestrictions())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
