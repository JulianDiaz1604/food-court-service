package com.pragma.foodcourt.infrastructure.configuration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Verifies JWTs issued by users-service with the shared HS256 secret and exposes the claims
 * (user id as subject, role name, optional restaurantId) needed to build the request's authentication.
 */
public class JwtTokenValidator {

    public static final String ROLE_CLAIM = "role";
    public static final String RESTAURANT_ID_CLAIM = "restaurantId";

    private final SecretKey signingKey;

    public JwtTokenValidator(String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return parseClaims(token).get(ROLE_CLAIM, String.class);
    }

    /**
     * Extracts the {@code restaurantId} claim from the token, or returns {@code null} when the
     * claim is absent (e.g. tokens issued before this claim was introduced — backward compatible).
     */
    public UUID extractRestaurantId(String token) {
        String value = parseClaims(token).get(RESTAURANT_ID_CLAIM, String.class);
        return value != null ? UUID.fromString(value) : null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
