package com.optimagrowth.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import brave.Tracer;

import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    // Sets the entry point to access trace and span ID information
    Tracer tracer;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Adds a span to the HTTP response header tmx-correlation-ID in the Spring Cloud Sleuth trace ID
                if (tracer.currentSpan()!=null) {
                    String traceId = tracer.currentSpan().context().traceIdString();
                    logger.debug("Adding the correlation id to the outbound headers. {}", traceId);
                    exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
                    logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
                }
            }));
        };
    }
}