package com.optimagrowth.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Order(1)
@Component
// pre-filter, called TrackingFilter, that will inspect all incoming requests to the gateway and determine whether
// there’s an HTTP header called tmx-correlation-id present in the request. The tmx-correlation-id header will
// contain a unique GUID (Globally Universal ID) that can be used to track a user’s request across multiple microservices
public class TrackingFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    // Commonly used functions across your filters are encapsulated in the FilterUtils class.
    @Autowired
    FilterUtils filterUtils;

    @Override
    // Code that executes every time a request passes through the filter
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Extracts the HTTP header from the request using the ServerWebExchange object passed by parameters to
        // the filter() method
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        // A helper method that checks if there’s a correlation ID in the request header
        if (isCorrelationIdPresent(requestHeaders)) {
            logger.debug("tmx-correlation-id found in tracking filter: {}. ",
                    filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationID);
            logger.debug("tmx-correlation-id generated in tracking filter: {}.", correlationID);
        }
        // Now that we have correlation IDs passed to each service, it’s possible to trace a
        //transaction as it flows through all the services involved in the call. To do this, you
        //need to ensure that each service logs to a central log aggregation point that captures
        //log entries from all of your services into a single point. Each log entry captured in the
        //log aggregation service will have a correlation ID associated with it.

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (filterUtils.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }

    // A helper method that checks if the tmx-correlation-id is present;
    // it can also generate a correlation ID UUID value
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

}