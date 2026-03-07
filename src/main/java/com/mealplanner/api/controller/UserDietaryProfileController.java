package com.mealplanner.api.controller;

import com.mealplanner.api.dto.UserDietaryProfileRequest;
import com.mealplanner.api.dto.UserDietaryProfileResponse;
import com.mealplanner.api.service.UserDietaryProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user dietary profiles.
 * Each user has one dietary profile with calorie goals,
 * macro targets, allergies, and dietary restrictions.
 */
@RestController
@RequestMapping("/api/dietary-profiles")
@RequiredArgsConstructor
public class UserDietaryProfileController {

    private final UserDietaryProfileService profileService;

    /** GET /api/dietary-profiles/{id} - Get a dietary profile by its ID */
    @GetMapping("/{id}")
    public ResponseEntity<UserDietaryProfileResponse> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    /** GET /api/dietary-profiles/user/{userId} - Get profile for a specific user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDietaryProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    /**
     * POST /api/dietary-profiles - Create a dietary profile for the authenticated user.
     * Each user can only have one profile.
     */
    @PostMapping
    public ResponseEntity<UserDietaryProfileResponse> createProfile(
            @Valid @RequestBody UserDietaryProfileRequest request,
            Authentication authentication) {
        UserDietaryProfileResponse response = profileService.createProfile(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** PUT /api/dietary-profiles/{id} - Update an existing dietary profile */
    @PutMapping("/{id}")
    public ResponseEntity<UserDietaryProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserDietaryProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(id, request));
    }

    /** DELETE /api/dietary-profiles/{id} - Delete a dietary profile */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
