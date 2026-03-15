package com.inventorymanagement.service;

import com.inventorymanagement.dto.SupplierDto;
import com.inventorymanagement.dto.SupplierResponseDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.exception.ResourceNotFoundException;
import com.inventorymanagement.model.Supplier;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for supplier management operations.
 *
 * Handles CRUD operations for suppliers, enforcing business rules
 * such as unique contact email addresses.
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public SupplierService(SupplierRepository supplierRepository,
                           ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierResponseDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return mapToResponseDto(supplier);
    }

    @Transactional
    public SupplierResponseDto createSupplier(SupplierDto supplierDto) {
        if (supplierRepository.existsByContactEmail(supplierDto.getContactEmail())) {
            throw new DuplicateResourceException("Supplier", "contactEmail", supplierDto.getContactEmail());
        }

        Supplier supplier = Supplier.builder()
                .name(supplierDto.getName())
                .contactEmail(supplierDto.getContactEmail())
                .phone(supplierDto.getPhone())
                .address(supplierDto.getAddress())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToResponseDto(savedSupplier);
    }

    /**
     * Updates an existing supplier.
     *
     * Contact email uniqueness is re-validated. If the email has not changed,
     * the check is effectively a no-op. If it has changed, we verify that no
     * other supplier already owns the new email address.
     *
     * @param id          the supplier ID to update
     * @param supplierDto the supplier update payload
     * @return the updated supplier as a response DTO
     * @throws ResourceNotFoundException  if no supplier exists with the given ID
     * @throws DuplicateResourceException if another supplier already uses the new email
     */
    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        // Check email uniqueness only if the email is being changed to a different value
        supplierRepository.findByContactEmail(supplierDto.getContactEmail())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new DuplicateResourceException("Supplier", "contactEmail", supplierDto.getContactEmail());
                    }
                });

        supplier.setName(supplierDto.getName());
        supplier.setContactEmail(supplierDto.getContactEmail());
        supplier.setPhone(supplierDto.getPhone());
        supplier.setAddress(supplierDto.getAddress());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapToResponseDto(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplierRepository.delete(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDto> searchSuppliers(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private SupplierResponseDto mapToResponseDto(Supplier supplier) {
        return SupplierResponseDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactEmail(supplier.getContactEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .productCount(productRepository.countBySupplier_Id(supplier.getId()))
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
