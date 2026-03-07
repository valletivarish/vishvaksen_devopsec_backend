package com.mealplanner.api.service;

import com.mealplanner.api.dto.IngredientRequest;
import com.mealplanner.api.dto.IngredientResponse;
import com.mealplanner.api.exception.DuplicateResourceException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.Ingredient;
import com.mealplanner.api.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Ingredient CRUD operations and search functionality.
 * Handles conversion between DTOs and entities, duplicate name checking,
 * and nutritional data management.
 */
@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    /** Retrieves all ingredients from the database */
    @Transactional(readOnly = true)
    public List<IngredientResponse> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Retrieves a single ingredient by its ID */
    @Transactional(readOnly = true)
    public IngredientResponse getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
        return toResponse(ingredient);
    }

    /** Searches ingredients by name containing the given keyword */
    @Transactional(readOnly = true)
    public List<IngredientResponse> searchIngredients(String keyword) {
        return ingredientRepository.searchByName(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new ingredient after checking for duplicate names.
     * All nutritional values are stored per 100 grams of the ingredient.
     */
    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        if (ingredientRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Ingredient already exists: " + request.getName());
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .carbs(request.getCarbs())
                .fat(request.getFat())
                .fiber(request.getFiber())
                .vitaminA(request.getVitaminA())
                .vitaminC(request.getVitaminC())
                .calcium(request.getCalcium())
                .iron(request.getIron())
                .unit(request.getUnit())
                .build();

        return toResponse(ingredientRepository.save(ingredient));
    }

    /** Updates an existing ingredient's nutritional data */
    @Transactional
    public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));

        ingredient.setName(request.getName());
        ingredient.setCalories(request.getCalories());
        ingredient.setProtein(request.getProtein());
        ingredient.setCarbs(request.getCarbs());
        ingredient.setFat(request.getFat());
        ingredient.setFiber(request.getFiber());
        ingredient.setVitaminA(request.getVitaminA());
        ingredient.setVitaminC(request.getVitaminC());
        ingredient.setCalcium(request.getCalcium());
        ingredient.setIron(request.getIron());
        ingredient.setUnit(request.getUnit());

        return toResponse(ingredientRepository.save(ingredient));
    }

    /** Deletes an ingredient by its ID */
    @Transactional
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient", id);
        }
        ingredientRepository.deleteById(id);
    }

    /** Converts Ingredient entity to IngredientResponse DTO */
    private IngredientResponse toResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .calories(ingredient.getCalories())
                .protein(ingredient.getProtein())
                .carbs(ingredient.getCarbs())
                .fat(ingredient.getFat())
                .fiber(ingredient.getFiber())
                .vitaminA(ingredient.getVitaminA())
                .vitaminC(ingredient.getVitaminC())
                .calcium(ingredient.getCalcium())
                .iron(ingredient.getIron())
                .unit(ingredient.getUnit())
                .createdAt(ingredient.getCreatedAt())
                .updatedAt(ingredient.getUpdatedAt())
                .build();
    }
}
