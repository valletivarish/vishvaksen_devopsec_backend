package com.mealplanner.api.controller;

import com.mealplanner.api.dto.IngredientRequest;
import com.mealplanner.api.dto.IngredientResponse;
import com.mealplanner.api.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Ingredient CRUD operations.
 * Manages the ingredient database with nutritional information.
 * Supports search by name keyword for recipe creation.
 */
@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    /** GET /api/ingredients - Retrieve all ingredients */
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    /** GET /api/ingredients/{id} - Retrieve a specific ingredient */
    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> getIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getIngredientById(id));
    }

    /** GET /api/ingredients/search?keyword=... - Search ingredients by name */
    @GetMapping("/search")
    public ResponseEntity<List<IngredientResponse>> searchIngredients(@RequestParam String keyword) {
        return ResponseEntity.ok(ingredientService.searchIngredients(keyword));
    }

    /** POST /api/ingredients - Create a new ingredient with nutritional data */
    @PostMapping
    public ResponseEntity<IngredientResponse> createIngredient(@Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.createIngredient(request));
    }

    /** PUT /api/ingredients/{id} - Update an existing ingredient */
    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, request));
    }

    /** DELETE /api/ingredients/{id} - Delete an ingredient */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
