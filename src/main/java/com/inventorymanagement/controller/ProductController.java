package com.inventorymanagement.controller;

import com.inventorymanagement.dto.ProductDto;
import com.inventorymanagement.dto.ProductResponseDto;
import com.inventorymanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for product management operations.
 * Provides CRUD endpoints along with search, filtering by category/supplier, and low-stock queries.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor injection for ProductService dependency.
     *
     * @param productService the service handling product business logic
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products in the inventory.
     *
     * @return a list of all products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a single product by its unique identifier.
     *
     * @param id the product ID
     * @return the matching product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Creates a new product in the inventory.
     *
     * @param productDto the product details to create
     * @return the created product with generated ID
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductResponseDto createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Updates an existing product with new details.
     *
     * @param id         the ID of the product to update
     * @param productDto the updated product details
     * @return the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @Valid @RequestBody ProductDto productDto) {
        ProductResponseDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Deletes a product from the inventory by its ID.
     *
     * @param id the ID of the product to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param categoryId the category ID to filter by
     * @return a list of products in the given category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves all products supplied by a specific supplier.
     *
     * @param supplierId the supplier ID to filter by
     * @return a list of products from the given supplier
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsBySupplierId(@PathVariable Long supplierId) {
        List<ProductResponseDto> products = productService.getProductsBySupplierId(supplierId);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves all products that are currently below the minimum stock threshold.
     * Useful for inventory alerts and reorder planning.
     *
     * @return a list of products with low stock levels
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDto>> getLowStockProducts() {
        List<ProductResponseDto> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Searches for products whose name matches the given query string.
     *
     * @param name the search term to match against product names
     * @return a list of products matching the search criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String name) {
        List<ProductResponseDto> products = productService.searchProducts(name);
        return ResponseEntity.ok(products);
    }
}
