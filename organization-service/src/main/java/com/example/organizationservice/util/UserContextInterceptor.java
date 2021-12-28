package com.example.organizationservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

// This class injects the correlation ID into any outgoing HTTP-based service request that’s executed from
// a RestTemplate instance. This is done to ensure that we can establish a link between service calls.
// To do this, we’ll use a Spring interceptor that’s injected into the RestTemplate class
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserContextInterceptor.class);

    @Override
    // Invokes intercept() before the actual HTTP service call occurs by the RestTemplate
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();
        // Takes the HTTP request header that’s being prepared for the outgoing service call and adds
        // the correlation ID stored in the UserContext
        headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());

        return execution.execute(request, body);
    }
}