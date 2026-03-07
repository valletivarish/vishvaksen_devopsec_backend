package com.mealplanner.api.service;

import com.mealplanner.api.dto.MealPlanRequest;
import com.mealplanner.api.dto.MealPlanResponse;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.MealPlan;
import com.mealplanner.api.model.User;
import com.mealplanner.api.repository.MealPlanRepository;
import com.mealplanner.api.repository.RecipeRepository;
import com.mealplanner.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MealPlanService covering CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
class MealPlanServiceTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MealPlanService mealPlanService;

    private User testUser;
    private MealPlan testPlan;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("testuser").fullName("Test User").build();

        testPlan = MealPlan.builder()
                .id(1L)
                .name("Week 1 Plan")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .user(testUser)
                .entries(new ArrayList<>())
                .build();
        testPlan.setCreatedAt(LocalDateTime.now());
        testPlan.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllMealPlans_returnsList() {
        when(mealPlanRepository.findAll()).thenReturn(List.of(testPlan));

        List<MealPlanResponse> result = mealPlanService.getAllMealPlans();

        assertFalse(result.isEmpty());
        assertEquals("Week 1 Plan", result.get(0).getName());
    }

    @Test
    void getMealPlanById_withValidId_returnsPlan() {
        when(mealPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        MealPlanResponse result = mealPlanService.getMealPlanById(1L);

        assertEquals("Week 1 Plan", result.getName());
    }

    @Test
    void getMealPlanById_withInvalidId_throwsException() {
        when(mealPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mealPlanService.getMealPlanById(99L));
    }

    @Test
    void createMealPlan_withValidData_returnsPlan() {
        MealPlanRequest request = new MealPlanRequest("New Plan", LocalDate.now(),
                LocalDate.now().plusDays(7), null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mealPlanRepository.save(any(MealPlan.class))).thenReturn(testPlan);

        MealPlanResponse result = mealPlanService.createMealPlan(request, "testuser");

        assertNotNull(result);
        verify(mealPlanRepository).save(any(MealPlan.class));
    }

    @Test
    void deleteMealPlan_withValidId_deletes() {
        when(mealPlanRepository.existsById(1L)).thenReturn(true);

        mealPlanService.deleteMealPlan(1L);

        verify(mealPlanRepository).deleteById(1L);
    }
}
