package com.pragma.foodcourt.infrastructure.out.client;

import com.pragma.foodcourt.domain.spi.IUserValidationPort;
import com.pragma.foodcourt.infrastructure.out.client.dto.UserClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 * Validates restaurant owners against the users-service over HTTP (Spring RestClient).
 * Implements the domain SPI port, keeping the transport out of the domain.
 */
@RequiredArgsConstructor
public class UserValidationRestClientAdapter implements IUserValidationPort {

    private static final Long OWNER_ROLE_ID = 2L;

    private final RestClient usersRestClient;

    @Override
    public boolean isOwner(UUID userId) {
        if (userId == null) {
            return false;
        }
        try {
            UserClientResponse user = usersRestClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .retrieve()
                    .body(UserClientResponse.class);
            return user != null && OWNER_ROLE_ID.equals(user.getRoleId());
        } catch (HttpClientErrorException.NotFound notFound) {
            return false;
        }
    }
}
