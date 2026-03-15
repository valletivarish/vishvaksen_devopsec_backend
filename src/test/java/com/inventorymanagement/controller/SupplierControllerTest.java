package com.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.SupplierDto;
import com.inventorymanagement.dto.SupplierResponseDto;
import com.inventorymanagement.service.SupplierService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SupplierController.class)
@AutoConfigureMockMvc(addFilters = false)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierService supplierService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private SupplierResponseDto createResponse(Long id, String name) {
        return SupplierResponseDto.builder()
                .id(id).name(name).contactEmail(name.toLowerCase() + "@example.com")
                .phone("+1234567890").address("123 Main St")
                .productCount(0L).createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("GET /api/suppliers returns 200 with list")
    void testGetAllSuppliers() throws Exception {
        when(supplierService.getAllSuppliers()).thenReturn(
                Arrays.asList(createResponse(1L, "Acme"), createResponse(2L, "Globex")));

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/suppliers/{id} returns 200")
    void testGetSupplierById() throws Exception {
        when(supplierService.getSupplierById(1L)).thenReturn(createResponse(1L, "Acme"));

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme"));
    }

    @Test
    @DisplayName("POST /api/suppliers returns 201 with valid data")
    void testCreateSupplier() throws Exception {
        SupplierDto dto = new SupplierDto("Acme Corp", "acme@example.com", "+1234567890", "123 Main St");
        when(supplierService.createSupplier(any(SupplierDto.class)))
                .thenReturn(createResponse(1L, "Acme Corp"));

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Acme Corp"));
    }

    @Test
    @DisplayName("POST /api/suppliers returns 400 when name is blank")
    void testCreateSupplier_ValidationError() throws Exception {
        SupplierDto dto = new SupplierDto("", "acme@example.com", "+1234567890", "123 Main St");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/suppliers/{id} returns 200")
    void testUpdateSupplier() throws Exception {
        SupplierDto dto = new SupplierDto("Updated Corp", "updated@example.com", "+1234567890", "456 Oak Ave");
        when(supplierService.updateSupplier(eq(1L), any(SupplierDto.class)))
                .thenReturn(createResponse(1L, "Updated Corp"));

        mockMvc.perform(put("/api/suppliers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Corp"));
    }

    @Test
    @DisplayName("DELETE /api/suppliers/{id} returns 204")
    void testDeleteSupplier() throws Exception {
        doNothing().when(supplierService).deleteSupplier(1L);

        mockMvc.perform(delete("/api/suppliers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/suppliers/search returns matching suppliers")
    void testSearchSuppliers() throws Exception {
        when(supplierService.searchSuppliers("Acme"))
                .thenReturn(Collections.singletonList(createResponse(1L, "Acme")));

        mockMvc.perform(get("/api/suppliers/search").param("name", "Acme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Acme"));
    }
}
