package com.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.CategoryDto;
import com.inventorymanagement.dto.CategoryResponseDto;
import com.inventorymanagement.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private CategoryResponseDto createResponse(Long id, String name) {
        return CategoryResponseDto.builder()
                .id(id).name(name).description("Test description")
                .productCount(0L).createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("GET /api/categories returns 200 with list")
    void testGetAllCategories() throws Exception {
        List<CategoryResponseDto> categories = Arrays.asList(
                createResponse(1L, "Electronics"),
                createResponse(2L, "Furniture"));
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} returns 200")
    void testGetCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(createResponse(1L, "Electronics"));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @DisplayName("POST /api/categories returns 201 with valid data")
    void testCreateCategory() throws Exception {
        CategoryDto dto = new CategoryDto("Electronics", "Electronic devices");
        when(categoryService.createCategory(any(CategoryDto.class)))
                .thenReturn(createResponse(1L, "Electronics"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @DisplayName("POST /api/categories returns 400 when name is blank")
    void testCreateCategory_ValidationError() throws Exception {
        CategoryDto dto = new CategoryDto("", "Description");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/categories/{id} returns 200")
    void testUpdateCategory() throws Exception {
        CategoryDto dto = new CategoryDto("Updated", "Updated description");
        when(categoryService.updateCategory(eq(1L), any(CategoryDto.class)))
                .thenReturn(createResponse(1L, "Updated"));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} returns 204")
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
