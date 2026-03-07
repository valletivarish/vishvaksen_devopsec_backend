package com.mealplanner.api.service;

import com.mealplanner.api.dto.IngredientRequest;
import com.mealplanner.api.dto.IngredientResponse;
import com.mealplanner.api.exception.DuplicateResourceException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.Ingredient;
import com.mealplanner.api.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IngredientService.
 * Tests CRUD operations, duplicate checking, and search functionality.
 */
@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private Ingredient testIngredient;
    private IngredientRequest testRequest;

    @BeforeEach
    void setUp() {
        testIngredient = Ingredient.builder()
                .id(1L)
                .name("Chicken Breast")
                .calories(165.0)
                .protein(31.0)
                .carbs(0.0)
                .fat(3.6)
                .fiber(0.0)
                .vitaminA(6.0)
                .vitaminC(0.0)
                .calcium(15.0)
                .iron(1.0)
                .unit("per 100g")
                .build();
        testIngredient.setCreatedAt(LocalDateTime.now());
        testIngredient.setUpdatedAt(LocalDateTime.now());

        testRequest = new IngredientRequest("Chicken Breast", 165.0, 31.0, 0.0, 3.6,
                0.0, 6.0, 0.0, 15.0, 1.0, "per 100g");
    }

    @Test
    void getAllIngredients_returnsListOfIngredients() {
        when(ingredientRepository.findAll()).thenReturn(List.of(testIngredient));

        List<IngredientResponse> result = ingredientService.getAllIngredients();

        assertFalse(result.isEmpty());
        assertEquals("Chicken Breast", result.get(0).getName());
        assertEquals(165.0, result.get(0).getCalories());
    }

    @Test
    void getIngredientById_withValidId_returnsIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));

        IngredientResponse result = ingredientService.getIngredientById(1L);

        assertEquals("Chicken Breast", result.getName());
        assertEquals(31.0, result.getProtein());
    }

    @Test
    void getIngredientById_withInvalidId_throwsException() {
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getIngredientById(99L));
    }

    @Test
    void createIngredient_withValidData_returnsCreatedIngredient() {
        when(ingredientRepository.existsByName("Chicken Breast")).thenReturn(false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(testIngredient);

        IngredientResponse result = ingredientService.createIngredient(testRequest);

        assertNotNull(result);
        assertEquals("Chicken Breast", result.getName());
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void createIngredient_withDuplicateName_throwsException() {
        when(ingredientRepository.existsByName("Chicken Breast")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> ingredientService.createIngredient(testRequest));
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void updateIngredient_withValidId_returnsUpdatedIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(testIngredient);

        IngredientResponse result = ingredientService.updateIngredient(1L, testRequest);

        assertNotNull(result);
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void deleteIngredient_withValidId_deletesSuccessfully() {
        when(ingredientRepository.existsById(1L)).thenReturn(true);

        ingredientService.deleteIngredient(1L);

        verify(ingredientRepository).deleteById(1L);
    }

    @Test
    void deleteIngredient_withInvalidId_throwsException() {
        when(ingredientRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.deleteIngredient(99L));
    }

    @Test
    void searchIngredients_returnsMatchingResults() {
        when(ingredientRepository.searchByName("chicken")).thenReturn(List.of(testIngredient));

        List<IngredientResponse> result = ingredientService.searchIngredients("chicken");

        assertFalse(result.isEmpty());
        assertEquals("Chicken Breast", result.get(0).getName());
    }
}
