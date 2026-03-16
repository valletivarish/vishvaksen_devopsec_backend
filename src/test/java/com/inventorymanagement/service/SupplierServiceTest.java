package com.inventorymanagement.service;

import com.inventorymanagement.dto.SupplierDto;
import com.inventorymanagement.dto.SupplierResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Supplier;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.SupplierRepository;
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
 * Unit tests for {@link SupplierService}.
 *
 * Verifies supplier CRUD operations, email uniqueness enforcement, and
 * correct DTO mapping including the computed productCount field.
 */
@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierDto supplierDto;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .name("Acme Corp")
                .contactEmail("acme@example.com")
                .phone("+1234567890")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .build();

        supplierDto = new SupplierDto("Acme Corp", "acme@example.com", "+1234567890", "123 Main St");
    }

    // -----------------------------------------------------------------------
    // getAllSuppliers
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllSuppliers returns all suppliers with product counts")
    void testGetAllSuppliers_Success() {
        Supplier supplier2 = Supplier.builder()
                .id(2L).name("Beta Inc").contactEmail("beta@example.com")
                .createdAt(LocalDateTime.now()).build();

        when(supplierRepository.findByDeletedFalse()).thenReturn(Arrays.asList(supplier, supplier2));
        when(productRepository.countBySupplier_IdAndDeletedFalse(1L)).thenReturn(3L);
        when(productRepository.countBySupplier_IdAndDeletedFalse(2L)).thenReturn(7L);

        List<SupplierResponseDto> result = supplierService.getAllSuppliers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Acme Corp");
        assertThat(result.get(0).getProductCount()).isEqualTo(3L);
        verify(supplierRepository, times(1)).findByDeletedFalse();
    }

    // -----------------------------------------------------------------------
    // createSupplier
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createSupplier persists a new supplier and returns the response DTO")
    void testCreateSupplier_Success() {
        when(supplierRepository.existsByContactEmailAndDeletedFalse("acme@example.com")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(productRepository.countBySupplier_IdAndDeletedFalse(anyLong())).thenReturn(0L);

        SupplierResponseDto result = supplierService.createSupplier(supplierDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Acme Corp");
        assertThat(result.getContactEmail()).isEqualTo("acme@example.com");
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("createSupplier throws DuplicateResourceException when email already exists")
    void testCreateSupplier_DuplicateEmail() {
        when(supplierRepository.existsByContactEmailAndDeletedFalse("acme@example.com")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.createSupplier(supplierDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Supplier already exists with contactEmail: acme@example.com");

        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    // -----------------------------------------------------------------------
    // updateSupplier
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateSupplier modifies an existing supplier and returns the updated DTO")
    void testUpdateSupplier_Success() {
        SupplierDto updateDto = new SupplierDto("Updated Corp", "updated@example.com", "+9876543210", "456 Oak Ave");

        Supplier updatedSupplier = Supplier.builder()
                .id(1L).name("Updated Corp").contactEmail("updated@example.com")
                .phone("+9876543210").address("456 Oak Ave").createdAt(LocalDateTime.now()).build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.findByContactEmailAndDeletedFalse("updated@example.com")).thenReturn(Optional.empty());
        when(supplierRepository.save(any(Supplier.class))).thenReturn(updatedSupplier);
        when(productRepository.countBySupplier_IdAndDeletedFalse(1L)).thenReturn(3L);

        SupplierResponseDto result = supplierService.updateSupplier(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Corp");
        assertThat(result.getContactEmail()).isEqualTo("updated@example.com");
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    // -----------------------------------------------------------------------
    // deleteSupplier
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteSupplier throws ResourceNotFoundException for a non-existent ID")
    void testDeleteSupplier_NotFound() {
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.deleteSupplier(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: 999");

        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    // -----------------------------------------------------------------------
    // getSupplierById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getSupplierById returns the correct supplier")
    void testGetSupplierById_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.countBySupplier_IdAndDeletedFalse(1L)).thenReturn(5L);

        SupplierResponseDto result = supplierService.getSupplierById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Acme Corp");
        assertThat(result.getProductCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("getSupplierById throws ResourceNotFoundException for non-existent ID")
    void testGetSupplierById_NotFound() {
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: 999");
    }

    // -----------------------------------------------------------------------
    // deleteSupplier -- success
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteSupplier soft-deletes the supplier when it exists")
    void testDeleteSupplier_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(1L);

        assertThat(supplier.isDeleted()).isTrue();
        assertThat(supplier.getDeletedAt()).isNotNull();
        verify(supplierRepository, times(1)).save(supplier);
    }

    // -----------------------------------------------------------------------
    // searchSuppliers
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("searchSuppliers returns matching suppliers")
    void testSearchSuppliers_Success() {
        when(supplierRepository.findByNameContainingIgnoreCaseAndDeletedFalse("acme"))
                .thenReturn(java.util.Collections.singletonList(supplier));
        when(productRepository.countBySupplier_IdAndDeletedFalse(1L)).thenReturn(2L);

        List<SupplierResponseDto> result = supplierService.searchSuppliers("acme");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Acme Corp");
    }
}
