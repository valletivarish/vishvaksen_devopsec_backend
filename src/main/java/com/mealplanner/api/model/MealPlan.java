package com.mealplanner.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MealPlan entity representing a weekly meal schedule for a user.
 * Contains meal plan entries that map specific recipes to days and meal types.
 * Users can create multiple meal plans for different weeks.
 */
@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Descriptive name for the meal plan (e.g., "Week 1 - January") */
    @Column(nullable = false, length = 200)
    private String name;

    /** Start date of the meal plan period */
    @Column(nullable = false)
    private LocalDate startDate;

    /** End date of the meal plan period */
    @Column(nullable = false)
    private LocalDate endDate;

    /** The user who owns this meal plan */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Individual meal entries mapping recipes to specific days and meal types */
    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealPlanEntry> entries = new ArrayList<>();

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
