package com.inventorymanagement.controller;

import com.inventorymanagement.dto.CategoryDto;
import com.inventorymanagement.dto.CategoryResponseDto;
import com.inventorymanagement.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * REST controller for category management operations.
 * Provides CRUD endpoints for organizing products into categories.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Constructor injection for CategoryService dependency.
     *
     * @param categoryService the service handling category business logic
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all product categories.
     *
     * @return a list of all categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a single category by its unique identifier.
     *
     * @param id the category ID
     * @return the matching category details
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Creates a new product category.
     *
     * @param categoryDto the category details to create
     * @return the created category with generated ID
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryResponseDto createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Updates an existing category with new details.
     *
     * @param id          the ID of the category to update
     * @param categoryDto the updated category details
     * @return the updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id,
                                                              @Valid @RequestBody CategoryDto categoryDto) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Deletes a category by its ID.
     * Products associated with this category should be reassigned or handled before deletion.
     *
     * @param id the ID of the category to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<CategoryResponseDto> toggleCategoryStatus(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesIncludingDeleted() {
        List<CategoryResponseDto> categories = categoryService.getAllCategoriesIncludingDeleted();
        return ResponseEntity.ok(categories);
    }
}
