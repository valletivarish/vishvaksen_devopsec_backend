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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        return supplierRepository.findByDeletedFalse().stream()
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
        if (supplierRepository.existsByContactEmailAndDeletedFalse(supplierDto.getContactEmail())) {
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

    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        supplierRepository.findByContactEmailAndDeletedFalse(supplierDto.getContactEmail())
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
        supplier.setDeleted(true);
        supplier.setDeletedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }

    @Transactional
    public SupplierResponseDto toggleSupplierStatus(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplier.setDeleted(!supplier.isDeleted());
        supplier.setDeletedAt(supplier.isDeleted() ? LocalDateTime.now() : null);
        supplierRepository.save(supplier);
        return mapToResponseDto(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDto> searchSuppliers(String name) {
        return supplierRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDto> getAllSuppliersIncludingDeleted() {
        return supplierRepository.findAll().stream()
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
                .productCount(productRepository.countBySupplier_IdAndDeletedFalse(supplier.getId()))
                .active(!supplier.isDeleted())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
