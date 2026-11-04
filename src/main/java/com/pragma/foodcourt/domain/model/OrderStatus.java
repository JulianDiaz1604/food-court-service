package com.pragma.foodcourt.domain.model;

/**
 * Lifecycle of an order. HU11 only creates orders in {@link #PENDING}; the remaining transitions
 * (IN_PREPARATION, READY, DELIVERED, CANCELLED) are implemented by their owning user stories.
 */
public enum OrderStatus {
    PENDING,
    IN_PREPARATION,
    READY,
    DELIVERED,
    CANCELLED
}
