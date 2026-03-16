package com.inventorymanagement.repository;

import com.inventorymanagement.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByDeletedFalse();

    Optional<Warehouse> findByNameIgnoreCase(String name);

    Optional<Warehouse> findByNameIgnoreCaseAndDeletedFalse(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    long countByDeletedFalse();
}
