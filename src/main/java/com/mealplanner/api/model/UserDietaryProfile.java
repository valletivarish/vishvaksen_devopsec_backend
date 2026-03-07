package com.mealplanner.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDietaryProfile stores a user's dietary preferences, restrictions,
 * and nutritional goals. Each user has exactly one dietary profile.
 * Allergies and restrictions are stored as element collections for
 * flexible querying and filtering.
 */
@Entity
@Table(name = "user_dietary_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDietaryProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** One-to-one relationship with the user who owns this profile */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Daily calorie intake goal, valid range 500-10000 */
    @Column(nullable = false)
    private Integer calorieGoal;

    /** Daily protein intake goal in grams */
    @Column(nullable = false)
    private Double proteinGoal;

    /** Daily carbohydrate intake goal in grams */
    @Column(nullable = false)
    private Double carbGoal;

    /** Daily fat intake goal in grams */
    @Column(nullable = false)
    private Double fatGoal;

    /** List of food allergies (e.g., peanuts, shellfish, dairy) */
    @ElementCollection
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "allergy")
    @Builder.Default
    private List<String> allergies = new ArrayList<>();

    /** List of dietary restrictions (e.g., VEGETARIAN, VEGAN, GLUTEN_FREE) */
    @ElementCollection
    @CollectionTable(name = "user_dietary_restrictions", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "restriction")
    @Builder.Default
    private List<String> dietaryRestrictions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
