package com.pragma.foodcourt.infrastructure.out.client;

import com.pragma.foodcourt.domain.exception.EmployeeAlreadyExistsException;
import com.pragma.foodcourt.domain.model.Employee;
import com.pragma.foodcourt.domain.spi.IUserCreationPort;
import com.pragma.foodcourt.infrastructure.out.client.dto.CreateEmployeeClientRequest;
import com.pragma.foodcourt.infrastructure.out.client.dto.UserClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 * Creates employee users in the users-service over HTTP (Spring RestClient). The owner's Bearer
 * token is forwarded by {@code usersRestClient} so the users-service authorizes the OWNER caller.
 * Keeps the transport out of the domain by implementing the {@link IUserCreationPort} SPI.
 */
@RequiredArgsConstructor
public class UserCreationRestClientAdapter implements IUserCreationPort {

    private static final String CREATE_EMPLOYEE_URI = "/api/v1/users/employee";

    private final RestClient usersRestClient;

    @Override
    public UUID createEmployee(Employee employee) {
        try {
            UserClientResponse created = usersRestClient.post()
                    .uri(CREATE_EMPLOYEE_URI)
                    .body(toClientRequest(employee))
                    .retrieve()
                    .body(UserClientResponse.class);
            return created == null ? null : created.getId();
        } catch (HttpClientErrorException.Conflict conflict) {
            throw new EmployeeAlreadyExistsException();
        }
    }

    private CreateEmployeeClientRequest toClientRequest(Employee employee) {
        return new CreateEmployeeClientRequest(
                employee.getName(),
                employee.getLastName(),
                employee.getDocumentNumber(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getPassword());
    }
}
