package com.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.StockMovementDto;
import com.inventorymanagement.dto.StockMovementResponseDto;
import com.inventorymanagement.model.MovementType;
import com.inventorymanagement.service.StockMovementService;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockMovementController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockMovementService stockMovementService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private StockMovementResponseDto createResponse(Long id, MovementType type) {
        return StockMovementResponseDto.builder()
                .id(id).productId(1L).productName("Test Product")
                .warehouseId(1L).warehouseName("Main Warehouse")
                .quantity(50).type(type).referenceNumber("REF-001")
                .notes("Test note").movementDate(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("GET /api/stock-movements returns 200 with list")
    void testGetAllMovements() throws Exception {
        when(stockMovementService.getAllMovements()).thenReturn(
                Arrays.asList(createResponse(1L, MovementType.IN), createResponse(2L, MovementType.OUT)));

        mockMvc.perform(get("/api/stock-movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/stock-movements/{id} returns 200")
    void testGetMovementById() throws Exception {
        when(stockMovementService.getMovementById(1L)).thenReturn(createResponse(1L, MovementType.IN));

        mockMvc.perform(get("/api/stock-movements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

    @Test
    @DisplayName("POST /api/stock-movements returns 201 with valid data")
    void testCreateMovement() throws Exception {
        StockMovementDto dto = new StockMovementDto(1L, 1L, 50, MovementType.IN, "REF-001", "Incoming stock");
        when(stockMovementService.createMovement(any(StockMovementDto.class)))
                .thenReturn(createResponse(1L, MovementType.IN));

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    @DisplayName("POST /api/stock-movements returns 400 when productId is null")
    void testCreateMovement_ValidationError() throws Exception {
        StockMovementDto dto = new StockMovementDto(null, 1L, 50, MovementType.IN, null, null);

        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/stock-movements/product/{productId} returns 200")
    void testGetMovementsByProduct() throws Exception {
        when(stockMovementService.getMovementsByProduct(1L))
                .thenReturn(Collections.singletonList(createResponse(1L, MovementType.IN)));

        mockMvc.perform(get("/api/stock-movements/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/stock-movements/warehouse/{warehouseId} returns 200")
    void testGetMovementsByWarehouse() throws Exception {
        when(stockMovementService.getMovementsByWarehouse(1L))
                .thenReturn(Collections.singletonList(createResponse(1L, MovementType.IN)));

        mockMvc.perform(get("/api/stock-movements/warehouse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/stock-movements/type/{type} returns 200")
    void testGetMovementsByType() throws Exception {
        when(stockMovementService.getMovementsByType(MovementType.IN))
                .thenReturn(Collections.singletonList(createResponse(1L, MovementType.IN)));

        mockMvc.perform(get("/api/stock-movements/type/IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/stock-movements/recent returns 200")
    void testGetRecentMovements() throws Exception {
        when(stockMovementService.getRecentMovements())
                .thenReturn(Collections.singletonList(createResponse(1L, MovementType.IN)));

        mockMvc.perform(get("/api/stock-movements/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
