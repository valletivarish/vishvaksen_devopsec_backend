package com.inventorymanagement.service;

import com.inventorymanagement.dto.ProductDto;
import com.inventorymanagement.dto.ProductResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Category;
import com.inventorymanagement.model.Product;
import com.inventorymanagement.model.Supplier;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProductService}.
 *
 * All repository dependencies are mocked so that these tests run in isolation
 * without a database. Each test exercises a single service method and verifies
 * both the returned result and the expected interactions with the repositories.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductService productService;

    // Shared test fixtures reused across multiple tests
    private Product product;
    private Category category;
    private Supplier supplier;
    private ProductDto productDto;

    /**
     * Initializes common test data before each test method.
     * Using a fresh set of objects per test prevents state leakage.
     */
    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .createdAt(LocalDateTime.now())
                .build();

        supplier = Supplier.builder()
                .id(1L)
                .name("Acme Corp")
                .contactEmail("acme@example.com")
                .phone("+1234567890")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .id(1L)
                .name("Wireless Keyboard")
                .sku("KB-WIRELESS-001")
                .description("Ergonomic wireless keyboard")
                .unitPrice(new BigDecimal("49.99"))
                .reorderLevel(10)
                .currentStock(50)
                .category(category)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productDto = new ProductDto(
                "Wireless Keyboard",
                "KB-WIRELESS-001",
                "Ergonomic wireless keyboard",
                new BigDecimal("49.99"),
                10,
                1L,
                1L
        );
    }

    // -----------------------------------------------------------------------
    // getAllProducts
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllProducts returns a list of all products mapped to DTOs")
    void testGetAllProducts_Success() {
        // Arrange -- repository returns two products
        Product product2 = Product.builder()
                .id(2L)
                .name("USB Mouse")
                .sku("MS-USB-001")
                .unitPrice(new BigDecimal("19.99"))
                .reorderLevel(5)
                .currentStock(100)
                .category(category)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .build();

        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(product, product2));

        // Act
        List<ProductResponseDto> result = productService.getAllProducts();

        // Assert -- verify size and that the repository was called exactly once
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Wireless Keyboard");
        assertThat(result.get(1).getName()).isEqualTo("USB Mouse");
        verify(productRepository, times(1)).findByDeletedFalse();
    }

    // -----------------------------------------------------------------------
    // getProductById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getProductById returns the correct product when it exists")
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto result = productService.getProductById(1L);

        // Assert -- verify all critical mapped fields
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Wireless Keyboard");
        assertThat(result.getSku()).isEqualTo("KB-WIRELESS-001");
        assertThat(result.getCategoryName()).isEqualTo("Electronics");
        assertThat(result.getSupplierName()).isEqualTo("Acme Corp");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProductById throws ResourceNotFoundException for a non-existent ID")
    void testGetProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }

    // -----------------------------------------------------------------------
    // createProduct
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createProduct persists a new product and returns the response DTO")
    void testCreateProduct_Success() {
        when(productRepository.findBySkuIgnoreCase("KB-WIRELESS-001")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto result = productService.createProduct(productDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Wireless Keyboard");
        assertThat(result.getSku()).isEqualTo("KB-WIRELESS-001");
        verify(productRepository, times(1)).findBySkuIgnoreCase("KB-WIRELESS-001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct throws DuplicateResourceException when SKU already exists")
    void testCreateProduct_DuplicateSku() {
        when(productRepository.findBySkuIgnoreCase("KB-WIRELESS-001")).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        assertThatThrownBy(() -> productService.createProduct(productDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Product already exists with sku: KB-WIRELESS-001");

        // Verify that save was never called because the validation failed first
        verify(productRepository, never()).save(any(Product.class));
    }

    // -----------------------------------------------------------------------
    // updateProduct
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProduct modifies an existing product and returns the updated DTO")
    void testUpdateProduct_Success() {
        ProductDto updateDto = new ProductDto(
                "Updated Keyboard", "KB-WIRELESS-002", "Updated description",
                new BigDecimal("59.99"), 15, 1L, 1L
        );

        Product updatedProduct = Product.builder()
                .id(1L).name("Updated Keyboard").sku("KB-WIRELESS-002")
                .description("Updated description").unitPrice(new BigDecimal("59.99"))
                .reorderLevel(15).currentStock(50).category(category).supplier(supplier)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySkuIgnoreCase("KB-WIRELESS-002")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponseDto result = productService.updateProduct(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Keyboard");
        assertThat(result.getUnitPrice()).isEqualByComparingTo(new BigDecimal("59.99"));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // -----------------------------------------------------------------------
    // deleteProduct
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteProduct soft-deletes the product when it exists")
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertThat(product.isDeleted()).isTrue();
        assertThat(product.getDeletedAt()).isNotNull();
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("deleteProduct throws ResourceNotFoundException for a non-existent ID")
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository, never()).save(any(Product.class));
    }

    // -----------------------------------------------------------------------
    // getLowStockProducts
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getLowStockProducts returns products at or below reorder level")
    void testGetLowStockProducts_Success() {
        Product lowStockProduct = Product.builder()
                .id(3L).name("Low Stock Item").sku("LS-001")
                .unitPrice(new BigDecimal("9.99")).reorderLevel(20).currentStock(5)
                .category(category).supplier(supplier).createdAt(LocalDateTime.now()).build();

        when(productRepository.findByCurrentStockLessThanEqualReorderLevel())
                .thenReturn(Collections.singletonList(lowStockProduct));

        List<ProductResponseDto> result = productService.getLowStockProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Low Stock Item");
        assertThat(result.get(0).getCurrentStock()).isLessThanOrEqualTo(result.get(0).getReorderLevel());
        verify(productRepository, times(1)).findByCurrentStockLessThanEqualReorderLevel();
    }

    // -----------------------------------------------------------------------
    // searchProducts
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("searchProducts returns products matching the name query")
    void testSearchProducts_Success() {
        when(productRepository.findByNameContainingIgnoreCaseAndDeletedFalse("keyboard"))
                .thenReturn(Collections.singletonList(product));

        List<ProductResponseDto> result = productService.searchProducts("keyboard");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).containsIgnoringCase("keyboard");
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndDeletedFalse("keyboard");
    }

    // -----------------------------------------------------------------------
    // getProductsByCategory
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getProductsByCategory returns products belonging to the category")
    void testGetProductsByCategory_Success() {
        when(productRepository.findByCategory_IdAndDeletedFalse(1L))
                .thenReturn(Collections.singletonList(product));

        List<ProductResponseDto> result = productService.getProductsByCategory(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo("Electronics");
        verify(productRepository, times(1)).findByCategory_IdAndDeletedFalse(1L);
    }

    // -----------------------------------------------------------------------
    // getProductsBySupplierId
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getProductsBySupplierId returns products from the supplier")
    void testGetProductsBySupplierId_Success() {
        when(productRepository.findBySupplier_IdAndDeletedFalse(1L))
                .thenReturn(Collections.singletonList(product));

        List<ProductResponseDto> result = productService.getProductsBySupplierId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSupplierName()).isEqualTo("Acme Corp");
        verify(productRepository, times(1)).findBySupplier_IdAndDeletedFalse(1L);
    }

    // -----------------------------------------------------------------------
    // updateProduct -- not found
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProduct throws ResourceNotFoundException for non-existent product")
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(999L, productDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository, never()).save(any(Product.class));
    }

    // -----------------------------------------------------------------------
    // updateProduct -- duplicate SKU
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProduct throws DuplicateResourceException when SKU conflicts with another product")
    void testUpdateProduct_DuplicateSku() {
        Product existingOther = Product.builder().id(2L).sku("KB-WIRELESS-001").build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySkuIgnoreCase("KB-WIRELESS-001")).thenReturn(Optional.of(existingOther));

        assertThatThrownBy(() -> productService.updateProduct(1L, productDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Product already exists with sku: KB-WIRELESS-001");
    }

    // -----------------------------------------------------------------------
    // mapToResponseDto -- null category and supplier
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllProducts handles product with null category and supplier gracefully")
    void testMapToResponseDto_NullCategoryAndSupplier() {
        Product productWithNulls = Product.builder()
                .id(3L)
                .name("Orphan Product")
                .sku("ORPHAN-001")
                .unitPrice(new BigDecimal("10.00"))
                .reorderLevel(5)
                .currentStock(20)
                .category(null)
                .supplier(null)
                .createdAt(LocalDateTime.now())
                .build();

        when(productRepository.findByDeletedFalse()).thenReturn(Collections.singletonList(productWithNulls));

        List<ProductResponseDto> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isNull();
        assertThat(result.get(0).getCategoryName()).isNull();
        assertThat(result.get(0).getSupplierId()).isNull();
        assertThat(result.get(0).getSupplierName()).isNull();
    }
}
