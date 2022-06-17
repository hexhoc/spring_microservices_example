package com.example.organizationservice.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
// The UserContext is a POJO class that contains all the specific data we want to store in the UserContextHolder.
// The UserContext class holds the HTTP header values for an individual service client request
// that is processed by our microservice
public class UserContext {
    // In these constants we define the names of the headers
    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN     = "tmx-auth-token";
    public static final String USER_ID        = "tmx-user-id";
    public static final String ORGANIZATION_ID = "tmx-organization-id";

    // Defining our variables as ThreadLocal lets us store data individually for the current thread. The information set
    // here can only be read by the thread that set the value.
    private static ThreadLocal<String> correlationId= new ThreadLocal<>();
    private static ThreadLocal<String> authToken= new ThreadLocal<>();
    private static ThreadLocal<String> userId = new ThreadLocal<>();
    private static ThreadLocal<String> organizationId = new ThreadLocal<>();

    public static String getCorrelationId() {
        return correlationId.get();
    }
    public static void setCorrelationId(String correlationId) {
        UserContext.correlationId.set(correlationId);
    }

    public static String getAuthToken() {
        return authToken.get();
    }
    public static void setAuthToken(String authToken) {
        UserContext.authToken.set(authToken);
    }

    public static String getUserId() {
        return userId.get();
    }
    public static void setUserId(String userId) {
        UserContext.userId.set(userId);
    }

    public static String getOrganizationId() {
        return organizationId.get();
    }
    public static void setOrganizationId(String organizationId) {
        UserContext.organizationId.set(organizationId);
    }

    public static HttpHeaders getHttpHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CORRELATION_ID, getCorrelationId());

        return httpHeaders;
    }
}