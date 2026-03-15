package com.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.ProductDto;
import com.inventorymanagement.dto.ProductResponseDto;
import com.inventorymanagement.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for {@link ProductController}.
 *
 * Uses @WebMvcTest to load only the web layer with security filters disabled.
 * The ProductService is mocked to isolate controller behavior.
 */
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /** Mock the product service -- controller delegates all logic here. */
    @MockBean
    private ProductService productService;

    /** Security beans must be mocked even with filters disabled to satisfy DI. */
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    // -----------------------------------------------------------------------
    // Shared helper to build a sample ProductResponseDto
    // -----------------------------------------------------------------------

    private ProductResponseDto buildProductResponse(Long id, String name, String sku) {
        return ProductResponseDto.builder()
                .id(id)
                .name(name)
                .sku(sku)
                .description("Test product")
                .unitPrice(new BigDecimal("49.99"))
                .reorderLevel(10)
                .categoryId(1L)
                .categoryName("Electronics")
                .supplierId(1L)
                .supplierName("Acme Corp")
                .currentStock(50)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // -----------------------------------------------------------------------
    // GET /api/products
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products returns 200 with a list of products")
    void testGetAllProducts_Success() throws Exception {
        List<ProductResponseDto> products = Arrays.asList(
                buildProductResponse(1L, "Keyboard", "KB-001"),
                buildProductResponse(2L, "Mouse", "MS-001")
        );

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Keyboard"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    // -----------------------------------------------------------------------
    // GET /api/products/{id}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products/{id} returns 200 with the requested product")
    void testGetProductById_Success() throws Exception {
        ProductResponseDto product = buildProductResponse(1L, "Keyboard", "KB-001");

        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.sku").value("KB-001"));
    }

    // -----------------------------------------------------------------------
    // POST /api/products
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/products returns 201 with valid product data")
    void testCreateProduct_Success() throws Exception {
        ProductDto request = new ProductDto(
                "Keyboard", "KB-001", "A keyboard", new BigDecimal("49.99"), 10, 1L, 1L
        );
        ProductResponseDto response = buildProductResponse(1L, "Keyboard", "KB-001");

        when(productService.createProduct(any(ProductDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.sku").value("KB-001"));
    }

    @Test
    @DisplayName("POST /api/products returns 400 when unit price is negative")
    void testCreateProduct_ValidationError() throws Exception {
        // Arrange -- negative price violates the @DecimalMin constraint
        ProductDto request = new ProductDto(
                "Keyboard", "KB-001", "A keyboard", new BigDecimal("-5.00"), 10, 1L, 1L
        );

        // Act & Assert -- the @Valid annotation should reject this request
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------------------------------------------------
    // DELETE /api/products/{id}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/products/{id} returns 204 on successful deletion")
    void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    // -----------------------------------------------------------------------
    // PUT /api/products/{id}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/products/{id} returns 200 with updated product")
    void testUpdateProduct_Success() throws Exception {
        ProductDto request = new ProductDto(
                "Updated KB", "KB-002", "Updated", new BigDecimal("59.99"), 15, 1L, 1L
        );
        ProductResponseDto response = buildProductResponse(1L, "Updated KB", "KB-002");

        when(productService.updateProduct(any(Long.class), any(ProductDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated KB"));
    }

    // -----------------------------------------------------------------------
    // GET /api/products/category/{categoryId}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products/category/{id} returns products by category")
    void testGetProductsByCategory_Success() throws Exception {
        List<ProductResponseDto> products = List.of(buildProductResponse(1L, "Keyboard", "KB-001"));

        when(productService.getProductsByCategory(1L)).thenReturn(products);

        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Keyboard"));
    }

    // -----------------------------------------------------------------------
    // GET /api/products/supplier/{supplierId}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products/supplier/{id} returns products by supplier")
    void testGetProductsBySupplierId_Success() throws Exception {
        List<ProductResponseDto> products = List.of(buildProductResponse(1L, "Mouse", "MS-001"));

        when(productService.getProductsBySupplierId(1L)).thenReturn(products);

        mockMvc.perform(get("/api/products/supplier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    // -----------------------------------------------------------------------
    // GET /api/products/low-stock
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products/low-stock returns low stock products")
    void testGetLowStockProducts_Success() throws Exception {
        List<ProductResponseDto> products = List.of(buildProductResponse(1L, "Low Item", "LS-001"));

        when(productService.getLowStockProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Low Item"));
    }

    // -----------------------------------------------------------------------
    // GET /api/products/search?name=...
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products/search returns matching products")
    void testSearchProducts_Success() throws Exception {
        List<ProductResponseDto> products = List.of(buildProductResponse(1L, "Keyboard", "KB-001"));

        when(productService.searchProducts("Key")).thenReturn(products);

        mockMvc.perform(get("/api/products/search").param("name", "Key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Keyboard"));
    }
}
