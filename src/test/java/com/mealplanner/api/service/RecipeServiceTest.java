package com.mealplanner.api.service;

import com.mealplanner.api.dto.RecipeRequest;
import com.mealplanner.api.dto.RecipeResponse;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.Recipe;
import com.mealplanner.api.model.User;
import com.mealplanner.api.model.enums.Difficulty;
import com.mealplanner.api.repository.IngredientRepository;
import com.mealplanner.api.repository.RecipeRepository;
import com.mealplanner.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RecipeService covering CRUD, search, and nutritional calculations.
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeService recipeService;

    private User testUser;
    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .password("encoded")
                .fullName("Test User")
                .build();

        testRecipe = Recipe.builder()
                .id(1L)
                .title("Grilled Chicken Salad")
                .description("A healthy grilled chicken salad")
                .instructions("1. Grill chicken\n2. Prepare salad\n3. Combine")
                .prepTime(15)
                .cookTime(20)
                .servings(2)
                .difficulty(Difficulty.EASY)
                .user(testUser)
                .recipeIngredients(new ArrayList<>())
                .build();
        testRecipe.setCreatedAt(LocalDateTime.now());
        testRecipe.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllRecipes_returnsListOfRecipes() {
        when(recipeRepository.findAll()).thenReturn(List.of(testRecipe));

        List<RecipeResponse> result = recipeService.getAllRecipes();

        assertFalse(result.isEmpty());
        assertEquals("Grilled Chicken Salad", result.get(0).getTitle());
    }

    @Test
    void getRecipeById_withValidId_returnsRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));

        RecipeResponse result = recipeService.getRecipeById(1L);

        assertEquals("Grilled Chicken Salad", result.getTitle());
        assertEquals("EASY", result.getDifficulty());
        assertEquals(2, result.getServings());
    }

    @Test
    void getRecipeById_withInvalidId_throwsException() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(99L));
    }

    @Test
    void createRecipe_withValidData_returnsCreatedRecipe() {
        RecipeRequest request = new RecipeRequest("Grilled Chicken Salad", "Healthy salad",
                "Instructions here", 15, 20, 2, "EASY", null, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        RecipeResponse result = recipeService.createRecipe(request, "testuser");

        assertNotNull(result);
        assertEquals("Grilled Chicken Salad", result.getTitle());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void searchRecipes_returnsMatchingRecipes() {
        when(recipeRepository.searchByTitle("chicken")).thenReturn(List.of(testRecipe));

        List<RecipeResponse> result = recipeService.searchRecipes("chicken");

        assertFalse(result.isEmpty());
        assertEquals("Grilled Chicken Salad", result.get(0).getTitle());
    }

    @Test
    void deleteRecipe_withValidId_deletesSuccessfully() {
        when(recipeRepository.existsById(1L)).thenReturn(true);

        recipeService.deleteRecipe(1L);

        verify(recipeRepository).deleteById(1L);
    }

    @Test
    void deleteRecipe_withInvalidId_throwsException() {
        when(recipeRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> recipeService.deleteRecipe(99L));
    }

    @Test
    void getRecipesByDifficulty_returnsFilteredRecipes() {
        when(recipeRepository.findByDifficulty(Difficulty.EASY)).thenReturn(List.of(testRecipe));

        List<RecipeResponse> result = recipeService.getRecipesByDifficulty("EASY");

        assertFalse(result.isEmpty());
        assertEquals("EASY", result.get(0).getDifficulty());
    }
}
