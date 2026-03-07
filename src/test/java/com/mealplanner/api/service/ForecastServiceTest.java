package com.mealplanner.api.service;

import com.mealplanner.api.dto.ForecastResponse;
import com.mealplanner.api.model.*;
import com.mealplanner.api.model.enums.Difficulty;
import com.mealplanner.api.model.enums.MealType;
import com.mealplanner.api.repository.MealPlanRepository;
import com.mealplanner.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ForecastService verifying ML prediction logic.
 */
@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ForecastService forecastService;

    @Test
    void generateForecast_withMealPlanData_returnsForecast() {
        User user = User.builder().id(1L).username("testuser").build();
        Ingredient chicken = Ingredient.builder()
                .id(1L).name("Chicken").calories(165.0).protein(31.0)
                .carbs(0.0).fat(3.6).fiber(0.0).vitaminA(6.0)
                .vitaminC(0.0).calcium(15.0).iron(1.0).unit("per 100g")
                .build();

        RecipeIngredient ri = RecipeIngredient.builder()
                .ingredient(chicken).quantity(200.0).unit("grams").build();

        Recipe recipe = Recipe.builder()
                .id(1L).title("Chicken Meal").difficulty(Difficulty.EASY)
                .prepTime(10).cookTime(20).servings(1).user(user)
                .recipeIngredients(new ArrayList<>(List.of(ri)))
                .build();

        MealPlanEntry entry = MealPlanEntry.builder()
                .recipe(recipe).dayOfWeek(DayOfWeek.MONDAY).mealType(MealType.LUNCH)
                .build();

        MealPlan plan = MealPlan.builder()
                .id(1L).name("Test Plan").startDate(LocalDate.now().minusDays(7))
                .endDate(LocalDate.now()).user(user)
                .entries(new ArrayList<>(List.of(entry)))
                .build();
        entry.setMealPlan(plan);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mealPlanRepository.findByUserId(1L)).thenReturn(List.of(plan));

        ForecastResponse result = forecastService.generateForecast("testuser");

        assertNotNull(result);
        assertNotNull(result.getTrendDirection());
        assertFalse(result.getForecasts().isEmpty());
        assertEquals(4, result.getForecasts().size());

        /* Verify forecast contains all nutrient types */
        assertEquals("Calories", result.getForecasts().get(0).getNutrientName());
        assertEquals("Protein", result.getForecasts().get(1).getNutrientName());
        assertEquals("Carbs", result.getForecasts().get(2).getNutrientName());
        assertEquals("Fat", result.getForecasts().get(3).getNutrientName());
    }

    @Test
    void generateForecast_withNoData_returnsEmptyForecast() {
        User user = User.builder().id(1L).username("testuser").build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mealPlanRepository.findByUserId(1L)).thenReturn(List.of());

        ForecastResponse result = forecastService.generateForecast("testuser");

        assertNotNull(result);
        assertFalse(result.getForecasts().isEmpty());
    }
}
