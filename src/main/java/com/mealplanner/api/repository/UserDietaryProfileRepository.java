package com.mealplanner.api.repository;

import com.mealplanner.api.model.UserDietaryProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserDietaryProfile entity.
 * Each user has at most one dietary profile (one-to-one relationship).
 */
@Repository
public interface UserDietaryProfileRepository extends JpaRepository<UserDietaryProfile, Long> {

    /** Find the dietary profile for a specific user */
    Optional<UserDietaryProfile> findByUserId(Long userId);

    /** Check if a user already has a dietary profile */
    boolean existsByUserId(Long userId);
}
