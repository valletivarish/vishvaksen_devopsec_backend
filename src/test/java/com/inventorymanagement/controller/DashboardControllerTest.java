package com.inventorymanagement.controller;

import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.DashboardDto;
import com.inventorymanagement.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/dashboard returns 200 with summary data")
    void testGetDashboardSummary() throws Exception {
        DashboardDto dto = DashboardDto.builder()
                .totalProducts(10L)
                .totalCategories(3L)
                .totalWarehouses(2L)
                .totalSuppliers(5L)
                .lowStockProducts(Collections.emptyList())
                .recentMovements(Collections.emptyList())
                .totalStockValue(new BigDecimal("50000.00"))
                .build();

        when(dashboardService.getDashboardSummary()).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(10))
                .andExpect(jsonPath("$.totalCategories").value(3))
                .andExpect(jsonPath("$.totalWarehouses").value(2))
                .andExpect(jsonPath("$.totalSuppliers").value(5))
                .andExpect(jsonPath("$.totalStockValue").value(50000.00));
    }
}
