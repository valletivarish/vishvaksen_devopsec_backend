package com.mealplanner.api.service;

import com.mealplanner.api.dto.ShoppingListRequest;
import com.mealplanner.api.dto.ShoppingListResponse;
import com.mealplanner.api.exception.ResourceNotFoundException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShoppingListService.
 * Covers CRUD, auto-generation from meal plans, toggle, and error paths.
 */
@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ShoppingListItemRepository shoppingListItemRepository;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingListService shoppingListService;

    private User user;
    private Ingredient ingredient;
    private ShoppingList shoppingList;
    private ShoppingListItem shoppingListItem;

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
                .calories(165.0)
                .protein(31.0)
                .carbs(0.0)
                .fat(3.6)
                .fiber(0.0)
                .vitaminA(0.0)
                .vitaminC(0.0)
                .calcium(0.0)
                .iron(0.0)
                .unit("grams")
                .build();

        shoppingListItem = ShoppingListItem.builder()
                .id(100L)
                .ingredient(ingredient)
                .quantity(200.0)
                .unit("grams")
                .checked(false)
                .build();

        shoppingList = ShoppingList.builder()
                .id(10L)
                .name("Weekly Shopping")
                .user(user)
                .mealPlan(null)
                .items(new ArrayList<>(Arrays.asList(shoppingListItem)))
                .build();
        shoppingList.setCreatedAt(LocalDateTime.now());
        shoppingList.setUpdatedAt(LocalDateTime.now());
        shoppingListItem.setShoppingList(shoppingList);
    }

    @Test
    void getShoppingListsByUser_returnsListOfResponses() {
        when(shoppingListRepository.findByUserId(1L)).thenReturn(Arrays.asList(shoppingList));

        List<ShoppingListResponse> results = shoppingListService.getShoppingListsByUser(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(10L);
        assertThat(results.get(0).getName()).isEqualTo("Weekly Shopping");
        assertThat(results.get(0).getUserId()).isEqualTo(1L);
        assertThat(results.get(0).getItems()).hasSize(1);
    }

    @Test
    void getShoppingListsByUser_whenNoLists_returnsEmptyList() {
        when(shoppingListRepository.findByUserId(99L)).thenReturn(List.of());

        List<ShoppingListResponse> results = shoppingListService.getShoppingListsByUser(99L);

        assertThat(results).isEmpty();
    }

    @Test
    void getAllShoppingLists_returnsAllLists() {
        when(shoppingListRepository.findAll()).thenReturn(Arrays.asList(shoppingList));

        List<ShoppingListResponse> results = shoppingListService.getAllShoppingLists();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Weekly Shopping");
    }

    @Test
    void getShoppingListById_whenExists_returnsResponse() {
        when(shoppingListRepository.findById(10L)).thenReturn(Optional.of(shoppingList));

        ShoppingListResponse result = shoppingListService.getShoppingListById(10L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Weekly Shopping");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getIngredientId()).isEqualTo(5L);
        assertThat(result.getItems().get(0).getIngredientName()).isEqualTo("Chicken");
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(200.0);
        assertThat(result.getItems().get(0).getChecked()).isFalse();
    }

    @Test
    void getShoppingListById_whenNotFound_throwsResourceNotFoundException() {
        when(shoppingListRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingListService.getShoppingListById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createShoppingList_withoutMealPlan_createsEmptyList() {
        ShoppingListRequest request = new ShoppingListRequest("My List", null);

        ShoppingList emptyShoppingList = ShoppingList.builder()
                .id(20L)
                .name("My List")
                .user(user)
                .mealPlan(null)
                .items(new ArrayList<>())
                .build();
        emptyShoppingList.setCreatedAt(LocalDateTime.now());
        emptyShoppingList.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(emptyShoppingList);

        ShoppingListResponse result = shoppingListService.createShoppingList(request, "testuser");

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getName()).isEqualTo("My List");
        assertThat(result.getMealPlanId()).isNull();
        assertThat(result.getItems()).isEmpty();
        verify(mealPlanRepository, never()).findById(any());
    }

    @Test
    void createShoppingList_withMealPlan_generatesItemsFromRecipes() {
        // Build the meal plan with entries
        Recipe recipe = Recipe.builder()
                .id(1L)
                .title("Chicken Salad")
                .recipeIngredients(new ArrayList<>())
                .build();

        RecipeIngredient ri = RecipeIngredient.builder()
                .ingredient(ingredient)
                .quantity(200.0)
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
                .id(2L)
                .name("Week 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(6))
                .user(user)
                .entries(new ArrayList<>(Arrays.asList(entry)))
                .build();

        ShoppingListRequest request = new ShoppingListRequest("Plan-based List", 2L);

        ShoppingList savedList = ShoppingList.builder()
                .id(30L)
                .name("Plan-based List")
                .user(user)
                .mealPlan(mealPlan)
                .items(Arrays.asList(shoppingListItem))
                .build();
        savedList.setCreatedAt(LocalDateTime.now());
        savedList.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mealPlanRepository.findById(2L)).thenReturn(Optional.of(mealPlan));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(savedList);

        ShoppingListResponse result = shoppingListService.createShoppingList(request, "testuser");

        assertThat(result.getId()).isEqualTo(30L);
        assertThat(result.getMealPlanId()).isEqualTo(2L);
        assertThat(result.getMealPlanName()).isEqualTo("Week 1");
    }

    @Test
    void createShoppingList_withMealPlan_mergesDuplicateIngredients() {
        // Two entries use the same ingredient
        Recipe recipe1 = Recipe.builder()
                .id(1L)
                .title("Recipe 1")
                .recipeIngredients(new ArrayList<>())
                .build();
        Recipe recipe2 = Recipe.builder()
                .id(2L)
                .title("Recipe 2")
                .recipeIngredients(new ArrayList<>())
                .build();

        RecipeIngredient ri1 = RecipeIngredient.builder()
                .ingredient(ingredient).quantity(100.0).unit("grams").build();
        RecipeIngredient ri2 = RecipeIngredient.builder()
                .ingredient(ingredient).quantity(150.0).unit("grams").build();

        recipe1.getRecipeIngredients().add(ri1);
        recipe2.getRecipeIngredients().add(ri2);

        MealPlanEntry entry1 = MealPlanEntry.builder().id(1L).recipe(recipe1)
                .dayOfWeek(DayOfWeek.MONDAY).mealType(MealType.LUNCH).build();
        MealPlanEntry entry2 = MealPlanEntry.builder().id(2L).recipe(recipe2)
                .dayOfWeek(DayOfWeek.TUESDAY).mealType(MealType.DINNER).build();

        MealPlan mealPlan = MealPlan.builder()
                .id(3L).name("Merge Test Plan")
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(6))
                .user(user)
                .entries(new ArrayList<>(Arrays.asList(entry1, entry2)))
                .build();

        ShoppingListRequest request = new ShoppingListRequest("Merged List", 3L);

        // We capture the list passed to save to verify merging happened
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mealPlanRepository.findById(3L)).thenReturn(Optional.of(mealPlan));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenAnswer(inv -> {
            ShoppingList sl = inv.getArgument(0);
            sl.setId(40L);
            sl.setCreatedAt(LocalDateTime.now());
            sl.setUpdatedAt(LocalDateTime.now());
            return sl;
        });

        ShoppingListResponse result = shoppingListService.createShoppingList(request, "testuser");

        // Only 1 unique ingredient, quantities merged to 250
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(250.0);
    }

    @Test
    void createShoppingList_whenUserNotFound_throwsResourceNotFoundException() {
        ShoppingListRequest request = new ShoppingListRequest("List", null);
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingListService.createShoppingList(request, "unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void createShoppingList_whenMealPlanNotFound_throwsResourceNotFoundException() {
        ShoppingListRequest request = new ShoppingListRequest("List", 999L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mealPlanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingListService.createShoppingList(request, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateShoppingList_whenExists_updatesName() {
        ShoppingListRequest request = new ShoppingListRequest("Updated List", null);

        ShoppingList updatedList = ShoppingList.builder()
                .id(10L).name("Updated List").user(user).items(new ArrayList<>()).build();
        updatedList.setCreatedAt(LocalDateTime.now());
        updatedList.setUpdatedAt(LocalDateTime.now());

        when(shoppingListRepository.findById(10L)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(updatedList);

        ShoppingListResponse result = shoppingListService.updateShoppingList(10L, request);

        assertThat(result.getName()).isEqualTo("Updated List");
    }

    @Test
    void updateShoppingList_whenNotFound_throwsResourceNotFoundException() {
        ShoppingListRequest request = new ShoppingListRequest("Updated List", null);
        when(shoppingListRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingListService.updateShoppingList(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void toggleItemChecked_whenItemExistsAndUnchecked_setsCheckedToTrue() {
        shoppingListItem.setChecked(false);
        when(shoppingListItemRepository.findById(100L)).thenReturn(Optional.of(shoppingListItem));

        shoppingListService.toggleItemChecked(100L);

        assertThat(shoppingListItem.getChecked()).isTrue();
        verify(shoppingListItemRepository).save(shoppingListItem);
    }

    @Test
    void toggleItemChecked_whenItemExistsAndChecked_setsCheckedToFalse() {
        shoppingListItem.setChecked(true);
        when(shoppingListItemRepository.findById(100L)).thenReturn(Optional.of(shoppingListItem));

        shoppingListService.toggleItemChecked(100L);

        assertThat(shoppingListItem.getChecked()).isFalse();
        verify(shoppingListItemRepository).save(shoppingListItem);
    }

    @Test
    void toggleItemChecked_whenItemNotFound_throwsResourceNotFoundException() {
        when(shoppingListItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingListService.toggleItemChecked(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteShoppingList_whenExists_deletesSuccessfully() {
        when(shoppingListRepository.existsById(10L)).thenReturn(true);

        shoppingListService.deleteShoppingList(10L);

        verify(shoppingListRepository).deleteById(10L);
    }

    @Test
    void deleteShoppingList_whenNotFound_throwsResourceNotFoundException() {
        when(shoppingListRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> shoppingListService.deleteShoppingList(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(shoppingListRepository, never()).deleteById(any());
    }

    @Test
    void getShoppingListById_withMealPlan_returnsMealPlanDetails() {
        MealPlan mealPlan = MealPlan.builder()
                .id(2L).name("Week 1")
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(6))
                .user(user).entries(new ArrayList<>()).build();

        ShoppingList listWithPlan = ShoppingList.builder()
                .id(15L).name("Plan List").user(user).mealPlan(mealPlan)
                .items(new ArrayList<>()).build();
        listWithPlan.setCreatedAt(LocalDateTime.now());
        listWithPlan.setUpdatedAt(LocalDateTime.now());

        when(shoppingListRepository.findById(15L)).thenReturn(Optional.of(listWithPlan));

        ShoppingListResponse result = shoppingListService.getShoppingListById(15L);

        assertThat(result.getMealPlanId()).isEqualTo(2L);
        assertThat(result.getMealPlanName()).isEqualTo("Week 1");
    }
}
