package com.inventorymanagement.service;

import com.inventorymanagement.dto.CategoryDto;
import com.inventorymanagement.dto.CategoryResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Category;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findByDeletedFalse().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(categoryDto.getName())) {
            throw new DuplicateResourceException("Category", "name", categoryDto.getName());
        }

        Category category = Category.builder()
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.findByNameIgnoreCaseAndDeletedFalse(categoryDto.getName())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new DuplicateResourceException("Category", "name", categoryDto.getName());
                    }
                });

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setDeleted(true);
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    @Transactional
    public CategoryResponseDto toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setDeleted(!category.isDeleted());
        category.setDeletedAt(category.isDeleted() ? LocalDateTime.now() : null);
        categoryRepository.save(category);
        return mapToResponseDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategoriesIncludingDeleted() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount(productRepository.countByCategory_IdAndDeletedFalse(category.getId()))
                .active(!category.isDeleted())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
