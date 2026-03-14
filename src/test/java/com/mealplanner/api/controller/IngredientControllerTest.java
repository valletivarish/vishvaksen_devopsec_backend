package com.mealplanner.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanner.api.dto.IngredientRequest;
import com.mealplanner.api.dto.IngredientResponse;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for IngredientController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngredientService ingredientService;

    private IngredientResponse ingredientResponse;
    private IngredientRequest ingredientRequest;

    @BeforeEach
    void setUp() {
        ingredientResponse = IngredientResponse.builder()
                .id(1L).name("Chicken Breast")
                .calories(165.0).protein(31.0).carbs(0.0).fat(3.6)
                .fiber(0.0).vitaminA(0.0).vitaminC(0.0).calcium(15.0).iron(1.0)
                .unit("grams")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        ingredientRequest = new IngredientRequest(
                "Chicken Breast", 165.0, 31.0, 0.0, 3.6, 0.0, 0.0, 0.0, 15.0, 1.0, "grams"
        );
    }

    @Test
    @WithMockUser
    void getAllIngredients_returnsList() throws Exception {
        when(ingredientService.getAllIngredients()).thenReturn(Arrays.asList(ingredientResponse));

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chicken Breast"))
                .andExpect(jsonPath("$[0].calories").value(165.0));
    }

    @Test
    @WithMockUser
    void getIngredientById_whenExists_returnsIngredient() throws Exception {
        when(ingredientService.getIngredientById(1L)).thenReturn(ingredientResponse);

        mockMvc.perform(get("/api/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chicken Breast"))
                .andExpect(jsonPath("$.unit").value("grams"));
    }

    @Test
    @WithMockUser
    void getIngredientById_whenNotFound_returnsNotFound() throws Exception {
        when(ingredientService.getIngredientById(99L))
                .thenThrow(new ResourceNotFoundException("Ingredient", 99L));

        mockMvc.perform(get("/api/ingredients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void searchIngredients_returnsMatches() throws Exception {
        when(ingredientService.searchIngredients("chicken")).thenReturn(Arrays.asList(ingredientResponse));

        mockMvc.perform(get("/api/ingredients/search").param("keyword", "chicken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chicken Breast"));
    }

    @Test
    @WithMockUser
    void createIngredient_withValidRequest_returnsCreated() throws Exception {
        when(ingredientService.createIngredient(any(IngredientRequest.class))).thenReturn(ingredientResponse);

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Chicken Breast"));
    }

    @Test
    @WithMockUser
    void createIngredient_withBlankName_returnsBadRequest() throws Exception {
        IngredientRequest invalid = new IngredientRequest(
                "", 165.0, 31.0, 0.0, 3.6, 0.0, 0.0, 0.0, 15.0, 1.0, "grams"
        );

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createIngredient_withNegativeCalories_returnsBadRequest() throws Exception {
        IngredientRequest invalid = new IngredientRequest(
                "Bad", -10.0, 31.0, 0.0, 3.6, 0.0, 0.0, 0.0, 15.0, 1.0, "grams"
        );

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateIngredient_withValidRequest_returnsOk() throws Exception {
        when(ingredientService.updateIngredient(eq(1L), any(IngredientRequest.class))).thenReturn(ingredientResponse);

        mockMvc.perform(put("/api/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chicken Breast"));
    }

    @Test
    @WithMockUser
    void updateIngredient_whenNotFound_returnsNotFound() throws Exception {
        when(ingredientService.updateIngredient(eq(99L), any(IngredientRequest.class)))
                .thenThrow(new ResourceNotFoundException("Ingredient", 99L));

        mockMvc.perform(put("/api/ingredients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteIngredient_whenExists_returnsNoContent() throws Exception {
        doNothing().when(ingredientService).deleteIngredient(1L);

        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteIngredient_whenNotFound_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Ingredient", 99L))
                .when(ingredientService).deleteIngredient(99L);

        mockMvc.perform(delete("/api/ingredients/99"))
                .andExpect(status().isNotFound());
    }
}
