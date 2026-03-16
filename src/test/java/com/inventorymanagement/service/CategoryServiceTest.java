package com.inventorymanagement.service;

import com.inventorymanagement.dto.CategoryDto;
import com.inventorymanagement.dto.CategoryResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Category;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CategoryService}.
 *
 * Verifies category CRUD operations, name uniqueness enforcement, and
 * correct DTO mapping including the computed productCount field.
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .createdAt(LocalDateTime.now())
                .build();

        categoryDto = new CategoryDto("Electronics", "Electronic devices and accessories");
    }

    // -----------------------------------------------------------------------
    // getAllCategories
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllCategories returns all categories with product counts")
    void testGetAllCategories_Success() {
        Category category2 = Category.builder()
                .id(2L).name("Furniture").description("Office furniture")
                .createdAt(LocalDateTime.now()).build();

        when(categoryRepository.findByDeletedFalse()).thenReturn(Arrays.asList(category, category2));
        when(productRepository.countByCategory_IdAndDeletedFalse(1L)).thenReturn(5L);
        when(productRepository.countByCategory_IdAndDeletedFalse(2L)).thenReturn(3L);

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        assertThat(result.get(0).getProductCount()).isEqualTo(5L);
        assertThat(result.get(1).getProductCount()).isEqualTo(3L);
        verify(categoryRepository, times(1)).findByDeletedFalse();
    }

    // -----------------------------------------------------------------------
    // getCategoryById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getCategoryById returns the correct category when it exists")
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.countByCategory_IdAndDeletedFalse(1L)).thenReturn(5L);

        CategoryResponseDto result = categoryService.getCategoryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
        assertThat(result.getProductCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("getCategoryById throws ResourceNotFoundException for a non-existent ID")
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");
    }

    // -----------------------------------------------------------------------
    // createCategory
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createCategory persists a new category and returns the response DTO")
    void testCreateCategory_Success() {
        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(productRepository.countByCategory_IdAndDeletedFalse(anyLong())).thenReturn(0L);

        CategoryResponseDto result = categoryService.createCategory(categoryDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("createCategory throws DuplicateResourceException when name already exists")
    void testCreateCategory_DuplicateName() {
        when(categoryRepository.existsByNameIgnoreCaseAndDeletedFalse("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(categoryDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category already exists with name: Electronics");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    // -----------------------------------------------------------------------
    // updateCategory
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateCategory modifies an existing category and returns the updated DTO")
    void testUpdateCategory_Success() {
        CategoryDto updateDto = new CategoryDto("Updated Electronics", "Updated description");

        Category updatedCategory = Category.builder()
                .id(1L).name("Updated Electronics").description("Updated description")
                .createdAt(LocalDateTime.now()).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameIgnoreCaseAndDeletedFalse("Updated Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(productRepository.countByCategory_IdAndDeletedFalse(1L)).thenReturn(5L);

        CategoryResponseDto result = categoryService.updateCategory(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    // -----------------------------------------------------------------------
    // deleteCategory
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteCategory soft-deletes the category when it exists")
    void testDeleteCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertThat(category.isDeleted()).isTrue();
        assertThat(category.getDeletedAt()).isNotNull();
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("deleteCategory throws ResourceNotFoundException for non-existent ID")
    void testDeleteCategory_NotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory throws ResourceNotFoundException for non-existent ID")
    void testUpdateCategory_NotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(999L, categoryDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory throws DuplicateResourceException when name conflicts with another category")
    void testUpdateCategory_DuplicateName() {
        Category existingOther = Category.builder().id(2L).name("Electronics").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameIgnoreCaseAndDeletedFalse("Electronics")).thenReturn(Optional.of(existingOther));

        CategoryDto updateDto = new CategoryDto("Electronics", "Same name");

        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category already exists with name: Electronics");
    }
}
