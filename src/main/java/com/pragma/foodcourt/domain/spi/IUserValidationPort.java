package com.pragma.foodcourt.domain.spi;

import java.util.UUID;

/**
 * Driven port to validate users living in the users-service.
 * The domain stays unaware of the transport (HTTP, messaging, ...).
 */
public interface IUserValidationPort {

    /**
     * @return true only when the given user exists and holds the OWNER role.
     */
    boolean isOwner(UUID userId);
}
