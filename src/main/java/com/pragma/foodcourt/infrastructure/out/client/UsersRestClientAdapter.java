package com.pragma.foodcourt.infrastructure.out.client;

import com.pragma.foodcourt.domain.exception.ClientNotFoundException;
import com.pragma.foodcourt.domain.exception.UserServiceUnavailableException;
import com.pragma.foodcourt.domain.model.UserContact;
import com.pragma.foodcourt.domain.spi.IUserClientPort;
import com.pragma.foodcourt.infrastructure.out.client.dto.UserContactResponseDto;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Infrastructure adapter that resolves a client's contact information from {@code users-service}.
 *
 * <ul>
 *   <li>{@code GET /api/v1/users/{clientId}} — retrieves phone and name</li>
 * </ul>
 *
 * The inbound {@code Authorization} header is forwarded via {@link AuthorizationForwardingInterceptor}
 * registered on the {@link RestClient} at construction time.
 */
public class UsersRestClientAdapter implements IUserClientPort {

    private static final String USER_PATH = "/api/v1/users/{clientId}";

    private final RestClient restClient;

    public UsersRestClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public UserContact getUserContactById(UUID clientId) {
        UserContactResponseDto response;
        try {
            response = restClient.get()
                    .uri(USER_PATH, clientId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            (req, res) -> { throw new ClientNotFoundException(); })
                    .body(UserContactResponseDto.class);
        } catch (RestClientException exception) {
            // Connection refused, timeout, or any non-404 error from users-service.
            // ClientNotFoundException is not a RestClientException, so it propagates untouched.
            throw new UserServiceUnavailableException();
        }

        if (response == null || response.getPhone() == null) {
            throw new UserServiceUnavailableException();
        }

        String fullName = buildFullName(response.getName(), response.getLastName());
        return new UserContact(response.getPhone(), fullName);
    }

    private String buildFullName(String name, String lastName) {
        if (name == null && lastName == null) {
            return null;
        }
        if (name == null) {
            return lastName;
        }
        if (lastName == null) {
            return name;
        }
        return name + " " + lastName;
    }
}
