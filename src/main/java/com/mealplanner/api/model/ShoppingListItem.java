package com.mealplanner.api.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Individual item within a shopping list, representing a specific
 * ingredient with its required quantity. Users can mark items as
 * checked while shopping to track purchasing progress.
 */
@Entity
@Table(name = "shopping_list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The shopping list this item belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

    /** The ingredient to purchase */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    /** Total quantity needed across all recipes in the meal plan */
    @Column(nullable = false)
    private Double quantity;

    /** Measurement unit for purchasing (e.g., grams, kg, pieces) */
    @Column(nullable = false, length = 50)
    private String unit;

    /** Whether the user has purchased this item */
    @Column(nullable = false)
    @Builder.Default
    private Boolean checked = false;
}
