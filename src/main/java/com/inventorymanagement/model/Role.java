package com.inventorymanagement.model;

/**
 * Enumeration representing the different authorization roles within the
 * Inventory Management System. Each role defines a distinct level of access
 * and set of permissions.
 *
 * <ul>
 *   <li><b>ADMIN</b>  - Full system access including user management, configuration,
 *                        and all CRUD operations across every module.</li>
 *   <li><b>MANAGER</b> - Operational access to manage products, suppliers, warehouses,
 *                        and stock movements, but no user-administration privileges.</li>
 *   <li><b>USER</b>    - Read-heavy access; can view inventory data and record basic
 *                        stock movements but cannot alter master data.</li>
 * </ul>
 */
public enum Role {

    /** Full administrative privileges across the entire system. */
    ADMIN,

    /** Operational management privileges for inventory-related modules. */
    MANAGER,

    /** Standard read and limited write access for day-to-day operations. */
    USER
}
