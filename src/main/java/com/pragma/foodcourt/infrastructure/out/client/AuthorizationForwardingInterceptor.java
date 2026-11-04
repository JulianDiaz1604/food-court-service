package com.pragma.foodcourt.infrastructure.out.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * Forwards the inbound request's {@code Authorization} header to the users-service calls, so the
 * downstream service can authorize the same authenticated caller (e.g. the OWNER creating an
 * employee). When there is no current HTTP request or no header, the call goes out unchanged.
 */
public class AuthorizationForwardingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(org.springframework.http.HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String authorization = currentAuthorizationHeader();
        if (authorization != null && !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorization);
        }
        return execution.execute(request, body);
    }

    private String currentAuthorizationHeader() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }
}
