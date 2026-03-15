package com.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.WarehouseDto;
import com.inventorymanagement.dto.WarehouseResponseDto;
import com.inventorymanagement.service.WarehouseService;
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

@WebMvcTest(WarehouseController.class)
@AutoConfigureMockMvc(addFilters = false)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WarehouseService warehouseService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private WarehouseResponseDto createResponse(Long id, String name) {
        return WarehouseResponseDto.builder()
                .id(id).name(name).location("Dublin, Ireland")
                .capacity(10000).currentUtilization(500)
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("GET /api/warehouses returns 200 with list")
    void testGetAllWarehouses() throws Exception {
        when(warehouseService.getAllWarehouses()).thenReturn(
                Arrays.asList(createResponse(1L, "Main"), createResponse(2L, "Secondary")));

        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/warehouses/{id} returns 200")
    void testGetWarehouseById() throws Exception {
        when(warehouseService.getWarehouseById(1L)).thenReturn(createResponse(1L, "Main"));

        mockMvc.perform(get("/api/warehouses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Main"));
    }

    @Test
    @DisplayName("POST /api/warehouses returns 201 with valid data")
    void testCreateWarehouse() throws Exception {
        WarehouseDto dto = new WarehouseDto("Main Warehouse", "Dublin, Ireland", 10000);
        when(warehouseService.createWarehouse(any(WarehouseDto.class)))
                .thenReturn(createResponse(1L, "Main Warehouse"));

        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Main Warehouse"));
    }

    @Test
    @DisplayName("POST /api/warehouses returns 400 when name is blank")
    void testCreateWarehouse_ValidationError() throws Exception {
        WarehouseDto dto = new WarehouseDto("", "Dublin", 10000);

        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/warehouses/{id} returns 200")
    void testUpdateWarehouse() throws Exception {
        WarehouseDto dto = new WarehouseDto("Updated Warehouse", "Cork, Ireland", 20000);
        when(warehouseService.updateWarehouse(eq(1L), any(WarehouseDto.class)))
                .thenReturn(createResponse(1L, "Updated Warehouse"));

        mockMvc.perform(put("/api/warehouses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Warehouse"));
    }

    @Test
    @DisplayName("DELETE /api/warehouses/{id} returns 204")
    void testDeleteWarehouse() throws Exception {
        doNothing().when(warehouseService).deleteWarehouse(1L);

        mockMvc.perform(delete("/api/warehouses/1"))
                .andExpect(status().isNoContent());
    }
}
