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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByDeletedFalse().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponseDto(product);
    }

    @Transactional
    public ProductResponseDto createProduct(ProductDto productDto) {
        if (productRepository.existsBySkuIgnoreCase(productDto.getSku())) {
            throw new DuplicateResourceException("Product", "sku", productDto.getSku());
        }

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", productDto.getSupplierId()));

        Product product = Product.builder()
                .name(productDto.getName())
                .sku(productDto.getSku())
                .description(productDto.getDescription())
                .unitPrice(productDto.getUnitPrice())
                .reorderLevel(productDto.getReorderLevel())
                .currentStock(0)
                .category(category)
                .supplier(supplier)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.findBySkuIgnoreCase(productDto.getSku())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new DuplicateResourceException("Product", "sku", productDto.getSku());
                    }
                });

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", productDto.getSupplierId()));

        product.setName(productDto.getName());
        product.setSku(productDto.getSku());
        product.setDescription(productDto.getDescription());
        product.setUnitPrice(productDto.getUnitPrice());
        product.setReorderLevel(productDto.getReorderLevel());
        product.setCategory(category);
        product.setSupplier(supplier);

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Transactional
    public ProductResponseDto toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setDeleted(!product.isDeleted());
        product.setDeletedAt(product.isDeleted() ? LocalDateTime.now() : null);
        productRepository.save(product);
        return mapToResponseDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_IdAndDeletedFalse(categoryId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsBySupplierId(Long supplierId) {
        return productRepository.findBySupplier_IdAndDeletedFalse(supplierId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getLowStockProducts() {
        return productRepository.findByCurrentStockLessThanEqualReorderLevel().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProductsIncludingDeleted() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .reorderLevel(product.getReorderLevel())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .currentStock(product.getCurrentStock())
                .active(!product.isDeleted())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
