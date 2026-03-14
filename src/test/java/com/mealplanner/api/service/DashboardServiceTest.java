package com.mealplanner.api.service;

import com.mealplanner.api.dto.DashboardResponse;
import com.mealplanner.api.model.*;
import com.mealplanner.api.model.enums.MealType;
import com.mealplanner.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DashboardService.
 * Covers summary statistics, nutritional calculations, and user-not-found error.
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User user;
    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        ingredient = Ingredient.builder()
                .id(5L)
                .name("Chicken")
                .calories(200.0)
                .protein(40.0)
                .carbs(0.0)
                .fat(5.0)
                .fiber(0.0)
                .vitaminA(0.0)
                .vitaminC(0.0)
                .calcium(0.0)
                .iron(0.0)
                .unit("grams")
                .build();
    }

    @Test
    void getDashboard_whenUserNotFound_throwsRuntimeException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.getDashboard("unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getDashboard_withNoMealPlans_returnsZeroedNutritionalData() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recipeRepository.countByUserId(1L)).thenReturn(5L);
        when(mealPlanRepository.countByUserId(1L)).thenReturn(0L);
        when(ingredientRepository.count()).thenReturn(20L);
        when(shoppingListRepository.findByUserId(1L)).thenReturn(List.of());
        when(mealPlanRepository.findByUserId(1L)).thenReturn(List.of());

        DashboardResponse result = dashboardService.getDashboard("testuser");

        assertThat(result.getTotalRecipes()).isEqualTo(5L);
        assertThat(result.getTotalMealPlans()).isEqualTo(0L);
        assertThat(result.getTotalIngredients()).isEqualTo(20L);
        assertThat(result.getTotalShoppingLists()).isEqualTo(0L);
        assertThat(result.getCaloriesByMealType()).isEmpty();
        assertThat(result.getMacroDistribution()).isEmpty();
        assertThat(result.getDailyNutrition()).isEmpty();
    }

    @Test
    void getDashboard_withMealPlanEntries_calculatesNutritionalBreakdown() {
        // Set up a recipe with one ingredient
        Recipe recipe = Recipe.builder()
                .id(1L)
                .title("Chicken Bowl")
                .recipeIngredients(new ArrayList<>())
                .build();

        // 100g of chicken: cal=200, pro=40, carbs=0, fat=5
        RecipeIngredient ri = RecipeIngredient.builder()
                .ingredient(ingredient)
                .quantity(100.0)   // factor = 100/100 = 1.0
                .unit("grams")
                .build();
        recipe.getRecipeIngredients().add(ri);

        MealPlanEntry entry = MealPlanEntry.builder()
                .id(1L)
                .recipe(recipe)
                .dayOfWeek(DayOfWeek.MONDAY)
                .mealType(MealType.LUNCH)
                .build();

        MealPlan mealPlan = MealPlan.builder()
                .id(1L)
                .name("Week 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(6))
                .user(user)
                .entries(new ArrayList<>(Arrays.asList(entry)))
                .build();
        entry.setMealPlan(mealPlan);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recipeRepository.countByUserId(1L)).thenReturn(3L);
        when(mealPlanRepository.countByUserId(1L)).thenReturn(1L);
        when(ingredientRepository.count()).thenReturn(10L);
        when(shoppingListRepository.findByUserId(1L)).thenReturn(List.of());
        when(mealPlanRepository.findByUserId(1L)).thenReturn(Arrays.asList(mealPlan));

        DashboardResponse result = dashboardService.getDashboard("testuser");

        assertThat(result.getTotalRecipes()).isEqualTo(3L);
        assertThat(result.getTotalMealPlans()).isEqualTo(1L);
        assertThat(result.getTotalIngredients()).isEqualTo(10L);

        // Calories by meal type: LUNCH = 200.0
        assertThat(result.getCaloriesByMealType()).containsKey("LUNCH");
        assertThat(result.getCaloriesByMealType().get("LUNCH")).isEqualTo(200.0);

        // Macro distribution: protein=40, carbs=0, fat=5 => total=45
        // Protein% = 40/45 * 100 ~ 88.9%, Fat% = 5/45 * 100 ~ 11.1%, Carbs% = 0%
        assertThat(result.getMacroDistribution()).containsKey("Protein");
        assertThat(result.getMacroDistribution()).containsKey("Carbs");
        assertThat(result.getMacroDistribution()).containsKey("Fat");
        assertThat(result.getMacroDistribution().get("Protein")).isGreaterThan(0.0);

        // Daily nutrition: MONDAY
        assertThat(result.getDailyNutrition()).hasSize(1);
        assertThat(result.getDailyNutrition().get(0).getDay()).isEqualTo("MONDAY");
        assertThat(result.getDailyNutrition().get(0).getCalories()).isEqualTo(200.0);
        assertThat(result.getDailyNutrition().get(0).getProtein()).isEqualTo(40.0);
        assertThat(result.getDailyNutrition().get(0).getCarbs()).isEqualTo(0.0);
        assertThat(result.getDailyNutrition().get(0).getFat()).isEqualTo(5.0);
    }

    @Test
    void getDashboard_usesLastMealPlanWhenMultipleExist() {
        Recipe recipe1 = Recipe.builder().id(1L).title("R1").recipeIngredients(new ArrayList<>()).build();
        Recipe recipe2 = Recipe.builder().id(2L).title("R2").recipeIngredients(new ArrayList<>()).build();

        RecipeIngredient ri2 = RecipeIngredient.builder()
                .ingredient(ingredient).quantity(50.0).unit("grams").build();
        recipe2.getRecipeIngredients().add(ri2);

        MealPlanEntry entry2 = MealPlanEntry.builder()
                .id(2L).recipe(recipe2)
                .dayOfWeek(DayOfWeek.FRIDAY).mealType(MealType.BREAKFAST).build();

        MealPlan plan1 = MealPlan.builder().id(1L).name("Old Plan")
                .startDate(LocalDate.now().minusDays(14)).endDate(LocalDate.now().minusDays(8))
                .user(user).entries(new ArrayList<>()).build();
        MealPlan plan2 = MealPlan.builder().id(2L).name("New Plan")
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(6))
                .user(user).entries(new ArrayList<>(Arrays.asList(entry2))).build();
        entry2.setMealPlan(plan2);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recipeRepository.countByUserId(1L)).thenReturn(2L);
        when(mealPlanRepository.countByUserId(1L)).thenReturn(2L);
        when(ingredientRepository.count()).thenReturn(15L);
        when(shoppingListRepository.findByUserId(1L)).thenReturn(List.of());
        when(mealPlanRepository.findByUserId(1L)).thenReturn(Arrays.asList(plan1, plan2));

        DashboardResponse result = dashboardService.getDashboard("testuser");

        // Should use plan2 (last), which has BREAKFAST entry
        assertThat(result.getCaloriesByMealType()).containsKey("BREAKFAST");
        assertThat(result.getDailyNutrition().get(0).getDay()).isEqualTo("FRIDAY");
    }

    @Test
    void getDashboard_withMultipleEntriesSameDay_aggregatesDailyNutrition() {
        Recipe recipe1 = Recipe.builder().id(1L).title("Breakfast").recipeIngredients(new ArrayList<>()).build();
        Recipe recipe2 = Recipe.builder().id(2L).title("Lunch").recipeIngredients(new ArrayList<>()).build();

        RecipeIngredient ri1 = RecipeIngredient.builder()
                .ingredient(ingredient).quantity(100.0).unit("grams").build();
        RecipeIngredient ri2 = RecipeIngredient.builder()
                .ingredient(ingredient).quantity(100.0).unit("grams").build();
        recipe1.getRecipeIngredients().add(ri1);
        recipe2.getRecipeIngredients().add(ri2);

        MealPlanEntry entry1 = MealPlanEntry.builder().id(1L).recipe(recipe1)
                .dayOfWeek(DayOfWeek.MONDAY).mealType(MealType.BREAKFAST).build();
        MealPlanEntry entry2 = MealPlanEntry.builder().id(2L).recipe(recipe2)
                .dayOfWeek(DayOfWeek.MONDAY).mealType(MealType.LUNCH).build();

        MealPlan mealPlan = MealPlan.builder().id(1L).name("Plan")
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(6))
                .user(user).entries(new ArrayList<>(Arrays.asList(entry1, entry2))).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recipeRepository.countByUserId(1L)).thenReturn(2L);
        when(mealPlanRepository.countByUserId(1L)).thenReturn(1L);
        when(ingredientRepository.count()).thenReturn(5L);
        when(shoppingListRepository.findByUserId(1L)).thenReturn(List.of());
        when(mealPlanRepository.findByUserId(1L)).thenReturn(Arrays.asList(mealPlan));

        DashboardResponse result = dashboardService.getDashboard("testuser");

        // Both entries are on MONDAY => one combined daily record
        assertThat(result.getDailyNutrition()).hasSize(1);
        assertThat(result.getDailyNutrition().get(0).getDay()).isEqualTo("MONDAY");
        // 2 * 200 = 400 calories total for MONDAY
        assertThat(result.getDailyNutrition().get(0).getCalories()).isEqualTo(400.0);

        // Two meal types
        assertThat(result.getCaloriesByMealType()).containsKeys("BREAKFAST", "LUNCH");
    }

    @Test
    void getDashboard_countsShoppingListsCorrectly() {
        ShoppingList sl1 = ShoppingList.builder().id(1L).name("List 1").user(user).items(new ArrayList<>()).build();
        ShoppingList sl2 = ShoppingList.builder().id(2L).name("List 2").user(user).items(new ArrayList<>()).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recipeRepository.countByUserId(1L)).thenReturn(0L);
        when(mealPlanRepository.countByUserId(1L)).thenReturn(0L);
        when(ingredientRepository.count()).thenReturn(0L);
        when(shoppingListRepository.findByUserId(1L)).thenReturn(Arrays.asList(sl1, sl2));
        when(mealPlanRepository.findByUserId(1L)).thenReturn(List.of());

        DashboardResponse result = dashboardService.getDashboard("testuser");

        assertThat(result.getTotalShoppingLists()).isEqualTo(2L);
    }

    @Test
    void getDashboard_withZeroMacros_doesNotPopulateMacroDistribution() {
        // An ingredient with all-zero macros
        Ingredient zeroIngredient = Ingredient.builder()
                .id(6L).name("Water")
                .calories(0.0).protein(0.0).carbs(0.0).fat(0.0)
                .fiber(0.0).vitaminA(0.0).vitaminC(0.0).calcium(0.0).iron(0.0)
                .unit("ml").build();

        Recipe recipe = Recipe.builder().id(3L).title("Water").recipeIngredients(new ArrayList<>()).build();
        RecipeIngredient ri = RecipeIngredient.builder()
                .ingredient(zeroIngredient).quantity(100.0).unit("ml").build();
        recipe.getRecipeIngredients().add(ri);

        MealPlanEntry entry = MealPlanEntry.builder().id(1L).recipe(recipe)
                .dayOfWeek(DayOfWeek.WEDNESDAY).mealType(MealType.SNACK).build();

        MealPlan plan = MealPlan.builder().id(1L).name("Zero Macro Plan")
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(6))
                .user(user).entries(new ArrayList<>(Arrays.asList(entry))).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recipeRepository.countByUserId(1L)).thenReturn(1L);
        when(mealPlanRepository.countByUserId(1L)).thenReturn(1L);
        when(ingredientRepository.count()).thenReturn(1L);
        when(shoppingListRepository.findByUserId(1L)).thenReturn(List.of());
        when(mealPlanRepository.findByUserId(1L)).thenReturn(Arrays.asList(plan));

        DashboardResponse result = dashboardService.getDashboard("testuser");

        // totalMacros = 0 so macroDistribution should remain empty
        assertThat(result.getMacroDistribution()).isEmpty();
    }
}
