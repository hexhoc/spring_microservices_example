package com.optimagrowth.license.service.client;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.util.UserContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.model.Organization;

@Component
public class OrganizationRestTemplateClient {

    // The Load Balancer–enabled RestTemplate class parses the URL passed into it and
    // uses whatever is passed in as the server name as the key to query the Load Balancer for
    // an instance of a service
    private final RestTemplate restTemplate;
    private final OrganizationRedisRepository redisRepository;
    private final Tracer tracer;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Autowired
    public OrganizationRestTemplateClient(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate,
                                          OrganizationRedisRepository redisRepository,
                                          Tracer tracer) {
        this.restTemplate = restTemplate;
        this.redisRepository = redisRepository;
        // Tracer accesses the Spring Cloud Sleuth trace information.
        this.tracer = tracer;
    }

    public Organization getOrganization(String organizationId){
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        Organization organization = checkRedisCache(organizationId);

        if (organization != null){
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);

        // When using a Load Balancer backed RestTemplate, builds the target URL
        // with the Eureka service ID
        ResponseEntity<Organization> restExchange =
                // The server name in the URL matches the application ID of the organization service
                // key that you used to register the organization service with Eureka:
                // http://{applicationid}/v1/organization/{organizationId}
                restTemplate.exchange(
                        "http://gatewayserver:8072/organization-service/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        /*Save the record from cache*/
        organization = restExchange.getBody();
        if (organization != null) {
            cacheOrganizationObject(organization);
        }

        return restExchange.getBody();
    }


    private Organization checkRedisCache(String organizationId) {
        // Use our own span to trace redis check request
        // Creates a custom span called readLicensingDataFromRedis
        ScopedSpan newSpan = tracer.startScopedSpan("readLicensingDataFromRedis");
        try {
            return redisRepository.findById(organizationId).orElse(null);
        }catch (Exception ex){
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", organizationId, ex);
            return null;
        }finally {
            // Adds tag information to the span and names the service that Zipkin will capture
            newSpan.tag("peer.service", "redis");
            newSpan.annotate("Client received");
            // Closes and finishes the span. If this is not done, we’ll get an error message in the
            // log saying that a span was left open.
            newSpan.finish();
        }
    }

    private void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        }catch (Exception ex){
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), ex);
        }
    }
}