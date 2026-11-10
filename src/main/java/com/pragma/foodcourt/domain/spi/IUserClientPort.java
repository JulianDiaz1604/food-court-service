package com.pragma.foodcourt.domain.spi;

import com.pragma.foodcourt.domain.model.UserContact;

import java.util.UUID;

/**
 * Driven port for resolving a client's contact information from the users-service.
 * Implementations live in {@code infrastructure/out/client}.
 */
public interface IUserClientPort {

    /**
     * Retrieves the phone number and name of the user identified by {@code clientId}.
     *
     * @param clientId the UUID of the user to look up
     * @return the user's contact information
     * @throws com.pragma.foodcourt.domain.exception.ClientNotFoundException         if the user does not
     *                                                                                exist in users-service
     * @throws com.pragma.foodcourt.domain.exception.UserServiceUnavailableException if users-service is
     *                                                                                unreachable or responds
     *                                                                                with an unexpected error
     */
    UserContact getUserContactById(UUID clientId);
}
