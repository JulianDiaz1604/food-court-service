package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.OrderStatus;

import java.util.UUID;

/**
 * Driven port for recording an order status change in traceability-service (HU17, criterion 1).
 *
 * <p>Recording is <strong>best-effort</strong>: the implementation must never propagate a failure,
 * so a traceability outage never blocks the order lifecycle. The call is made after the transition
 * has been persisted.</p>
 */
public interface IOrderTraceabilityPort {

    /**
     * Records one order status change.
     *
     * @param orderId        the order that changed
     * @param clientId       the order's owning client
     * @param previousStatus the status before the change ({@code null} for order creation)
     * @param newStatus      the status after the change
     * @param employeeId     the employee who triggered it ({@code null} for client-driven changes)
     */
    void recordStatusChange(UUID orderId, UUID clientId, OrderStatus previousStatus,
                            OrderStatus newStatus, UUID employeeId);
}
