package com.inventorymanagement.model;

/**
 * Enumeration representing the type of stock movement that occurs within
 * the Inventory Management System. Every {@link StockMovement} record is
 * tagged with one of these types to provide an accurate audit trail of
 * how and why inventory quantities change.
 *
 * <ul>
 *   <li><b>IN</b>       - Goods received into a warehouse (e.g., purchase orders,
 *                          returns from customers).</li>
 *   <li><b>OUT</b>      - Goods dispatched from a warehouse (e.g., sales orders,
 *                          write-offs, damages).</li>
 *   <li><b>TRANSFER</b> - Goods moved between warehouses; typically paired as an
 *                          OUT from the source and an IN at the destination.</li>
 * </ul>
 */
public enum MovementType {

    /** Inbound movement -- stock entering a warehouse. */
    IN,

    /** Outbound movement -- stock leaving a warehouse. */
    OUT,

    /** Inter-warehouse transfer of stock. */
    TRANSFER
}
