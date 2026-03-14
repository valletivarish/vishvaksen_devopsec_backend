package com.mealplanner.api.controller;

import com.mealplanner.api.dto.DashboardResponse;
import com.mealplanner.api.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for DashboardController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    @WithMockUser(username = "testuser")
    void getDashboard_returnsOkWithDashboardData() throws Exception {
        DashboardResponse response = DashboardResponse.builder()
                .totalRecipes(5L).totalMealPlans(2L)
                .totalIngredients(30L).totalShoppingLists(3L)
                .caloriesByMealType(Map.of("LUNCH", 500.0, "DINNER", 700.0))
                .macroDistribution(Map.of("Protein", 35.0, "Carbs", 45.0, "Fat", 20.0))
                .dailyNutrition(Arrays.asList(
                        DashboardResponse.DailyNutrition.builder()
                                .day("MONDAY").calories(1200.0).protein(80.0).carbs(150.0).fat(30.0).build()
                ))
                .build();

        when(dashboardService.getDashboard("testuser")).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecipes").value(5L))
                .andExpect(jsonPath("$.totalMealPlans").value(2L))
                .andExpect(jsonPath("$.dailyNutrition[0].day").value("MONDAY"));
    }

    @Test
    @WithMockUser(username = "newuser")
    void getDashboard_whenNoData_returnsZeroedResponse() throws Exception {
        DashboardResponse empty = DashboardResponse.builder()
                .totalRecipes(0L).totalMealPlans(0L)
                .totalIngredients(0L).totalShoppingLists(0L)
                .caloriesByMealType(Map.of()).macroDistribution(Map.of())
                .dailyNutrition(List.of()).build();

        when(dashboardService.getDashboard("newuser")).thenReturn(empty);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecipes").value(0L))
                .andExpect(jsonPath("$.dailyNutrition").isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getDashboard_returnsJsonContentType() throws Exception {
        DashboardResponse response = DashboardResponse.builder()
                .totalRecipes(1L).totalMealPlans(1L)
                .totalIngredients(10L).totalShoppingLists(1L)
                .caloriesByMealType(Map.of()).macroDistribution(Map.of())
                .dailyNutrition(List.of()).build();

        when(dashboardService.getDashboard("testuser")).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}
