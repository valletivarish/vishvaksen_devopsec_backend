package com.mealplanner.api.model;

import com.mealplanner.api.model.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe entity representing a cooking recipe with its ingredients.
 * Contains cooking instructions, preparation details, and links to
 * ingredients through RecipeIngredient join entities for quantity tracking.
 */
@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Title of the recipe displayed in listings and search results */
    @Column(nullable = false, length = 200)
    private String title;

    /** Brief description of the dish and its characteristics */
    @Column(length = 2000)
    private String description;

    /** Step-by-step cooking instructions stored as text */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    /** Preparation time in minutes before cooking starts */
    @Column(nullable = false)
    private Integer prepTime;

    /** Active cooking time in minutes */
    @Column(nullable = false)
    private Integer cookTime;

    /** Number of servings this recipe yields */
    @Column(nullable = false)
    private Integer servings;

    /** Difficulty level to help users filter by skill level */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    /** Optional URL to a recipe image for display */
    @Column(length = 500)
    private String imageUrl;

    /** The user who created this recipe */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** List of ingredients with quantities used in this recipe */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

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
