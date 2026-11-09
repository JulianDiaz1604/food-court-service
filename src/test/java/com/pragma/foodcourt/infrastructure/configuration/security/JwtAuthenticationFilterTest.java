package com.pragma.foodcourt.infrastructure.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JwtAuthenticationFilter}.
 * Verifies that the filter builds a typed {@link AuthenticatedUser} principal with the correct
 * userId, role, and optional restaurantId extracted from the JWT claims (JI-S01, JI-S02, JI-S08–S10).
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtTokenValidator);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenTokenHasRestaurantIdClaimThenPrincipalExposesTypedUserWithRestaurantId()
            throws Exception {
        // JI-S01, JI-S08: EMPLOYEE token with restaurantId claim
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        String token = "valid.employee.token";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtTokenValidator.isTokenValid(token)).thenReturn(true);
        when(jwtTokenValidator.extractUserId(token)).thenReturn(userId);
        when(jwtTokenValidator.extractRole(token)).thenReturn("EMPLOYEE");
        when(jwtTokenValidator.extractRestaurantId(token)).thenReturn(restaurantId);

        filter.doFilterInternal(request, response, chain);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertInstanceOf(AuthenticatedUser.class, principal);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) principal;
        assertEquals(userId, authenticatedUser.userId());
        assertEquals("EMPLOYEE", authenticatedUser.role());
        assertEquals(restaurantId, authenticatedUser.restaurantId());
    }

    @Test
    void whenTokenHasNoRestaurantIdClaimThenPrincipalExposesNullRestaurantId()
            throws Exception {
        // JI-S02, JI-S09: CLIENT token without restaurantId claim
        UUID userId = UUID.randomUUID();
        String token = "valid.client.token";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtTokenValidator.isTokenValid(token)).thenReturn(true);
        when(jwtTokenValidator.extractUserId(token)).thenReturn(userId);
        when(jwtTokenValidator.extractRole(token)).thenReturn("CLIENT");
        when(jwtTokenValidator.extractRestaurantId(token)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertInstanceOf(AuthenticatedUser.class, principal);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) principal;
        assertEquals(userId, authenticatedUser.userId());
        assertEquals("CLIENT", authenticatedUser.role());
        assertNull(authenticatedUser.restaurantId());
    }

    @Test
    void whenTokenIsInvalidThenSecurityContextRemainsEmpty() throws Exception {
        // JI-S10: tampered or expired token is rejected — no authentication set
        String token = "invalid.tampered.token";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtTokenValidator.isTokenValid(token)).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void whenNoAuthorizationHeaderThenSecurityContextRemainsEmpty() throws Exception {
        // JI-S09: request with no token at all — no authentication set
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
