package com.optimagrowth.license.service.client;

import com.optimagrowth.license.config.ServiceConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    private RestTemplate restTemplate;
    private ServiceConfig serviceConfig;

    @Autowired
    public OrganizationRestTemplateClient(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate,
                                          ServiceConfig serviceConfig) {
        this.restTemplate = restTemplate;
        this.serviceConfig = serviceConfig;
    }

    public Organization getOrganization(String organizationId){
        // When using a Load Balancer backed RestTemplate, builds the target URL
        // with the Eureka service ID
        ResponseEntity<Organization> restExchange =
                // The server name in the URL matches the application ID of the organization service
                // key that you used to register the organization service with Eureka:
                // http://{applicationid}/v1/organization/{organizationId}
                restTemplate.exchange(
                        serviceConfig.getGateway()+"/organization-service/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}