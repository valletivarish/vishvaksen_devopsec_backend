package com.inventorymanagement.config;

import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.StockMovementRepository;
import com.inventorymanagement.repository.SupplierRepository;
import com.inventorymanagement.repository.UserRepository;
import com.inventorymanagement.repository.WarehouseRepository;
import com.inventorymanagement.model.User;
import com.inventorymanagement.model.Category;
import com.inventorymanagement.model.Supplier;
import com.inventorymanagement.model.Warehouse;
import com.inventorymanagement.model.Product;
import com.inventorymanagement.model.StockMovement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DataInitializer}.
 *
 * Verifies that demo data is seeded only when the database is empty and
 * that seeding is skipped when data already exists.
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    @DisplayName("run seeds demo data when database is empty")
    void testRun_SeedsData() {
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> {
            Supplier s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(inv -> {
            Warehouse w = inv.getArgument(0);
            w.setId(1L);
            return w;
        });
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        verify(userRepository, times(2)).save(any(User.class));
        verify(categoryRepository, times(4)).save(any(Category.class));
        verify(supplierRepository, times(3)).save(any(Supplier.class));
        verify(warehouseRepository, times(3)).save(any(Warehouse.class));
        verify(productRepository, times(6)).save(any(Product.class));
        verify(stockMovementRepository, times(4)).save(any(StockMovement.class));
        verify(passwordEncoder, times(2)).encode(anyString());
    }

    @Test
    @DisplayName("run skips seeding when database already contains data")
    void testRun_SkipsSeedingWhenDataExists() {
        when(userRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(userRepository, never()).save(any(User.class));
        verify(categoryRepository, never()).save(any(Category.class));
        verify(supplierRepository, never()).save(any(Supplier.class));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
        verify(productRepository, never()).save(any(Product.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }
}
